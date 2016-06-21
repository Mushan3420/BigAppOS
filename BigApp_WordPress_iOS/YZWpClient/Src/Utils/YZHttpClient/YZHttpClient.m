//
//  YZHttpClient.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.

#import "YZHttpClient.h"
#import "AFNetworking.h"
#import "BuildConfig.h"
#import "Additions.h"
#import "YZLoginController.h"
#import "Config.h"

#define kErrorUserAuthenticationRequired    @"5015"
#define kErrorClientCannotDecodeContentData 601

#define kYZResponseCode    @"error_code"  //00表示成功，其他为错误  （不可空）
#define kYZRsponseMsg      @"error_msg"
#define kYZResponseData    @"data"
#import "AppConfigManager.h"

#if !TARGET_ENV_PRD

@implementation NSURLRequest (IgnoreSSL)

+ (BOOL)allowsAnyHTTPSCertificateForHost:(NSString*)host
{
    return YES; // 测试环境允许无效证书
}

@end

#endif


@implementation AFHTTPRequestOperationManager (requestMethod)

- (AFHTTPRequestOperation *)sendRequest:(NSString *)URLString
                          requestMethod:(NSString *)requestMethod
                             parameters:(id)parameters
                                success:(void (^)(AFHTTPRequestOperation *operation, id responseObject))success
                                failure:(void (^)(AFHTTPRequestOperation *operation, NSError *error))failure
{
    
    
    NSError *serializationError = nil;
    NSMutableURLRequest *request = [self.requestSerializer requestWithMethod:requestMethod URLString:[[NSURL URLWithString:URLString relativeToURL:self.baseURL] absoluteString] parameters:parameters error:&serializationError];
    if (serializationError) {
        if (failure) {

            dispatch_async(dispatch_get_main_queue(), ^{
                failure(nil, serializationError);
            });
        }
        
        return nil;
    }
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request success:success failure:failure];
    
    
    [self.operationQueue addOperation:operation];
    
    return operation;
    
}

@end

static inline NSString * HTTPUserAgent() {

    NSString *userAgent = [NSString stringWithFormat:@"%@/%@ (%@; iPhone iOS %@; Scale/%0.2f)", [[[NSBundle mainBundle] infoDictionary] objectForKey:(__bridge NSString *)kCFBundleExecutableKey] ?: [[[NSBundle mainBundle] infoDictionary] objectForKey:(__bridge NSString *)kCFBundleIdentifierKey], [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"] ?: [[[NSBundle mainBundle] infoDictionary] objectForKey:(__bridge NSString *)kCFBundleVersionKey], [[UIDevice currentDevice] model], [[UIDevice currentDevice] systemVersion], [[UIScreen mainScreen] scale]];
    
    if (userAgent){
        if (![userAgent canBeConvertedToEncoding:NSASCIIStringEncoding]){
            NSMutableString *mutableUserAgent = [userAgent mutableCopy];
            
            if (CFStringTransform((__bridge CFMutableStringRef)(mutableUserAgent), NULL, (__bridge CFStringRef)@"Any-Latin; Latin-ASCII; [:^ASCII:] Remove", false)){
                userAgent = mutableUserAgent;
            }
        }
    }
    
    return userAgent;
}

@implementation YZHttpClient

/**
 @param requestMethod支持GET和POST请求
 */
+ (NSOperation *)requestWithUrl:(NSString *)url
                  requestMethod:(NSString *)requestMethod
                     parameters:(NSDictionary *)parameters
                        success:(void (^)(id responseObject))success
                        failure:(void (^)(NSString *statusCode, NSString *error))failure
{
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];

#if !TARGET_ENV_PRD
    manager.securityPolicy.allowInvalidCertificates = YES;  // 测试环境允许无效证书
#endif
    
    // 设置请求
    AFHTTPRequestSerializer *requestSerializer = [AFHTTPRequestSerializer serializer];
    requestSerializer.timeoutInterval = 15;
    NSString *userAgent = HTTPUserAgent();
    [requestSerializer setValue:userAgent forHTTPHeaderField:@"User-Agent"];
    NSString *appVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    [requestSerializer setValue:appVersion forHTTPHeaderField:@"Version"];
    [requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Accept"];
  
    manager.requestSerializer = requestSerializer;
    
    // 设置响应
    AFHTTPResponseSerializer *responseSerializer = [AFJSONResponseSerializer serializer];
    NSMutableSet *acceptableContentTypes = [NSMutableSet setWithSet:responseSerializer.acceptableContentTypes];
    [acceptableContentTypes addObject:@"text/plain"];
    [acceptableContentTypes addObject:@"text/html"];
    
    responseSerializer.acceptableContentTypes = acceptableContentTypes;
    manager.responseSerializer = responseSerializer;
    
    //url 可能是个完整的url
    
    NSURL *nsurl = [NSURL URLWithString:url];
    if (!nsurl.host) {
        url = [NSString stringWithFormat:@"%@%@",[AppConfigManager sharedInstance].appHttpServer,url];
        url= [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];

    }
    
    NSOperation *operation = [manager sendRequest:url requestMethod:requestMethod parameters:parameters success:^(AFHTTPRequestOperation *operation, id responseObject) {
        
        if (!responseObject){
            NSInteger errorCode = kErrorClientCannotDecodeContentData;
            NSString *errorMsg = @"The response data cannot be decoded.";
            
            DLog(@"\n%@ FAILED: \n%@\nERROR_CODE: %ld\nERROR: %@", requestMethod, [url urlByAppendingDictNoEncode:parameters], (long)errorCode, errorMsg);
            
            NSString *tError = [NSString stringWithFormat:@"系统繁忙，请稍后再试(N%ld)", (long)errorCode];
            
            if (failure){
                dispatch_async(dispatch_get_main_queue(), ^{
                    failure([NSString stringWithFormat:@"%@",@(errorCode)], tError);
                });
            }
        }
        else{
            DLog(@"\n%@ SUCCESS: \n%@\nRESPONSE: \n%@", requestMethod, [url urlByAppendingDictNoEncode:parameters], [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:responseObject options:NSJSONWritingPrettyPrinted error:NULL] encoding:NSUTF8StringEncoding]);
            
            NSNumber *errorCode = [responseObject objectForKey:kYZResponseCode];
            
            if ([errorCode isKindOfClass:[NSNumber class]]&&[errorCode isEqualToNumber:@0]){
                if (success){
                    dispatch_async(dispatch_get_main_queue(), ^{
                        success(responseObject[kYZResponseData]);
                    });
                }
            }
            else{
                if ([errorCode isEqual:@30]) {
                    [Config clearCookie];

                    //必须是注册用户，所以必须登录
                    YZLoginController *login= [YZLoginController new];
                    [[UIApplication sharedApplication].keyWindow.rootViewController presentViewController:login
                                                                                                 animated:YES completion:^{
                                                                                                 }];
                    return ;
                }
                
                if (failure){
                    NSString *tError = [responseObject objectForKey:kYZRsponseMsg];
                    dispatch_async(dispatch_get_main_queue(), ^{
                        failure([NSString stringWithFormat:@"%@",errorCode], tError);
                    });
                }
            }

        }
        
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        
        NSString *tError = nil;
        
        switch ([error code]) {
                
            case NSURLErrorTimedOut:
                tError = [NSString stringWithFormat:@"连接服务器超时(%ld), 请检查你的网络或稍后重试", (long)[error code]];
                break;
                
            case NSURLErrorBadURL:
            case NSURLErrorUnsupportedURL:
            case NSURLErrorCannotFindHost:
            case NSURLErrorDNSLookupFailed:
            case NSURLErrorCannotConnectToHost:
            case NSURLErrorNetworkConnectionLost:
            case NSURLErrorNotConnectedToInternet:
            case NSURLErrorUserCancelledAuthentication:
            case NSURLErrorUserAuthenticationRequired:
            case NSURLErrorSecureConnectionFailed:
            case NSURLErrorServerCertificateHasBadDate:
            case NSURLErrorServerCertificateUntrusted:
            case NSURLErrorServerCertificateHasUnknownRoot:
            case NSURLErrorServerCertificateNotYetValid:
            case NSURLErrorClientCertificateRejected:
            case NSURLErrorClientCertificateRequired:
                tError = [NSString stringWithFormat:@"无法连接到服务器(%ld), 请检查你的网络或稍后重试", (long)[error code]];
                break;
                
            case NSURLErrorHTTPTooManyRedirects:
                tError = [NSString stringWithFormat:@"太多HTTP重定向(%ld), 请检查你的网络或稍后重试", (long)[error code]];
                break;
                
            default:
                tError = [NSString stringWithFormat:@"系统繁忙，请稍后再试(%ld)", (long)[error code]];
                break;
        }
        
        
        DLog(@"\n%@ FAILED: \n%@\nERROR_CODE: %ld\nERROR: %@", requestMethod, [url urlByAppendingDictNoEncode:parameters], (long)[error code], tError);
        
        if (failure){
            dispatch_async(dispatch_get_main_queue(), ^{
                failure([NSString stringWithFormat:@"%@",@([error code])], tError);
            });
        }

        
    }];
    
    return operation;
    
}


+(NSString *)parserData:(NSString *)name
{
    NSString *resourcePath = [[NSBundle mainBundle] resourcePath];
    
    NSString *path = [resourcePath stringByAppendingPathComponent:name];
    
    NSData *data = [NSData dataWithContentsOfFile:path];
    
    NSString *ordersString= [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    
    return ordersString;
    
}


- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)aResponse{
    NSMutableData *returnInfoData=[[NSMutableData alloc]init];
//    totalSize= [aResponse expectedContentLength];
    NSHTTPURLResponse * httpResponse;
    httpResponse = (NSHTTPURLResponse *)aResponse;
    if ((httpResponse.statusCode / 100) != 2) {
        NSLog(@"保存失败");
    } else {
        NSLog(@"保存成功");
    }
}

+(void)uploadImage:(UIImage *)image returnInfo:(void (^)(NSInteger errorcode,NSString *avatarUrl))result
{
    NSString *strURL=[NSString stringWithFormat:@"%@/?yz_app=1&api_route=users&action=upload_avatar",[AppConfigManager sharedInstance].appHttpServer];// 这里用图片为

    NSString *TWITTERFON_FORM_BOUNDARY = @"0xKhTmLbOuNdArY";
    //根据url初始化request
    NSMutableURLRequest* request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:strURL]
                                                           cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                                       timeoutInterval:10];
    //分界线 --AaB03x
    NSString *MPboundary=[[NSString alloc]initWithFormat:@"--%@",TWITTERFON_FORM_BOUNDARY];
    //结束符 AaB03x--
    NSString *endMPboundary=[[NSString alloc]initWithFormat:@"%@--",MPboundary];
    //得到图片的data
    NSData *data;
    NSString *fileType = @"jpg";
    //判断图片是不是png格式的文件
    if (UIImagePNGRepresentation(image)) {
        //返回为png图像。
        data = UIImagePNGRepresentation([self imageWithImageSimple:image scaledToSize:CGSizeMake(100, 100)]);
        fileType = @"png";
    }else {
        //返回为JPEG图像。
        data = UIImageJPEGRepresentation(image, 1.0);
        fileType = @"jpg";
    }
    
    //文件名
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"yyyyMMddHHmmssSSS";
    NSString *str = [formatter stringFromDate:[NSDate date]];
    NSString *fileName = [NSString stringWithFormat:@"%@_%@.%@", @([Config getUserID]), str,fileType];
    
    //http body的字符串
    NSMutableString *body=[[NSMutableString alloc]init];
    
    
    ////添加分界线，换行
    [body appendFormat:@"%@\r\n",MPboundary];
    
    //声明pic字段，文件名为boris.png
    [body appendFormat:@"Content-Disposition: form-data; name=\"%@\"; filename=\"%@\"\r\n",@"wpua-file",fileName];
    //声明上传文件的格式
    [body appendFormat:@"Content-Type: image/png\r\n\r\n"];
    
    //声明结束符：--AaB03x--
    NSString *end=[[NSString alloc]initWithFormat:@"\r\n%@",endMPboundary];
    //声明myRequestData，用来放入http body
    NSMutableData *myRequestData=[NSMutableData data];
    
    //将body字符串转化为UTF8格式的二进制
    [myRequestData appendData:[body dataUsingEncoding:NSUTF8StringEncoding]];
    [myRequestData appendData:data];
    //加入结束符--AaB03x--
    [myRequestData appendData:[end dataUsingEncoding:NSUTF8StringEncoding]];
    
    //设置HTTPHeader中Content-Type的值
    NSString *content=[[NSString alloc]initWithFormat:@"multipart/form-data; boundary=%@",TWITTERFON_FORM_BOUNDARY];
    //设置HTTPHeader
    [request setValue:content forHTTPHeaderField:@"Content-Type"];
    //设置Content-Length
    [request setValue:[NSString stringWithFormat:@"%@", @([myRequestData length])] forHTTPHeaderField:@"Content-Length"];
    //设置http body
    [request setHTTPBody:myRequestData];
    //http method
    [request setHTTPMethod:@"POST"];
    
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSHTTPURLResponse *urlResponese = nil;
        NSError *error = [[NSError alloc]init];
        NSData* resultData = [NSURLConnection sendSynchronousRequest:request   returningResponse:&urlResponese error:&error];
        
        
        
        dispatch_async(dispatch_get_main_queue(), ^{
            //系统自带JSON解析
            NSDictionary *resultDic = [NSJSONSerialization JSONObjectWithData:resultData options:NSJSONReadingMutableLeaves error:nil];
            NSString *errorcode = [resultDic objectForKey:@"error_code"];
            if(errorcode.integerValue == 0){
                DLog(@"返回结果=====%@",result);
                result(errorcode.integerValue,[resultDic objectForKey:@"data"]);
                
            }
            else
            {
                result(errorcode.integerValue,[resultDic objectForKey:@"error_msg"]);
            }
            
            
        });
        
        
    });
    
    
    
}

/**
 * 修发图片大小
 */
+ (UIImage *) imageWithImageSimple:(UIImage*)image scaledToSize:(CGSize) newSize{
    newSize.height=image.size.height*(newSize.width/image.size.width);
    UIGraphicsBeginImageContext(newSize);
    [image drawInRect:CGRectMake(0, 0, newSize.width, newSize.height)];
    UIImage *newImage=UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return  newImage;
    
}

@end
