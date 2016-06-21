//
//  YZOffLineDownload.m
//  YZWpClient
//
//  Created by zhoutl on 15/10/22.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZOffLineDownloadTool.h"
#import "YZHomeService.h"
#import "PINCache.h"
#import "YZCacheHelper.h"
#import "YZNavListModel.h"
#import "MJExtension.h"
#import "YZPostsModel.h"
#import "SDWebImageManager.h"

@implementation YZOffLineDownloadTool


+ (void)startOffLine:(void (^)(NSString *))callback{
    
    
    
    YZOffLineDownloadTool *downTool = [[YZOffLineDownloadTool alloc] init];
    downTool.callback = callback;
    
    NSArray *navListsArray = [downTool getAllOffLineCategoryModels];
    
    
    [navListsArray enumerateObjectsUsingBlock:^(YZNavListModel *navModel, NSUInteger idx, BOOL * _Nonnull stop) {
        
        NSLog(@"====正在离线--%@...----",navModel.name);
        
        NSString *offlneStauts = [NSString stringWithFormat:@"正在离线 %@...",navModel.name];
        
        callback(offlneStauts);
        
        [downTool offlineAppointUrl:navModel.link];
        
    }];
    

    
}

- (id)objectForKey:(NSString *)key{
    
    if (!key) return nil;
    
    return [[PINCache sharedCache] objectForKey:key];
}//根据key 获取缓存内容


- (NSArray *)getAllOffLineCategoryModels{
    
    NSArray *navList = [self objectForKey:[YZCacheHelper getNavListCacheKey]];
    
    NSMutableArray *navListsArray =[YZNavListModel objectArrayWithKeyValuesArray:navList];
    
    return navListsArray;
}//获取所有需要离线的 分类


- (void)offlineAppointUrl:(NSString *)url{
    //每次离线指定分类的最新15条文章
    [YZHomeService doPostsListPageCounts:@"15"
                             currentPage:@"1"
                             categoryUrl:url
                                 success:^(NSArray *postsList)
     {
         [postsList enumerateObjectsUsingBlock:^(YZPostsModel *postModel, NSUInteger idx, BOOL * _Nonnull stop) {
             NSLog(@"====开始缓存第-%@-篇文章的图片----",@(idx));
             [self downLoadArticeAllImages:postModel.all_article_images];

         }];
         
         
     } failure:^(NSString *statusCode, NSString *error) {
        
         
     }];
}

- (void)downLoadArticeAllImages:(NSArray *)imageUrls{
    
    if (imageUrls.count == 0) {
        return;
    }
    
    SDWebImageManager *manager = [SDWebImageManager sharedManager];
    
     NSLog(@"====开始缓存图片----");
    [imageUrls enumerateObjectsUsingBlock:^(NSString  *imageUrlString, NSUInteger idx, BOOL *stop) {
        
        
        if (![imageUrlString isKindOfClass:[NSNull class]]) {
            NSString *urlStr= [imageUrlString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            NSURL *url = [NSURL URLWithString:imageUrlString];
            if (!url) {
                url = [NSURL URLWithString:urlStr];
            }
            [manager downloadImageWithURL:url options:SDWebImageHighPriority progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
                if (image) {
                    
                    NSLog(@"====第%@张缓存成功----",@(idx));
                    
                    NSString *offlneStauts = [NSString stringWithFormat:@"下载图片 %@/%@...",@(idx),@(imageUrls.count)];
                    
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        _callback(offlneStauts);
                        
                    });

                }
                else{
                    NSLog(@"====第%@张缓存失败----",@(idx));
                }
                
                
            }];
        }
        
        
    }];
}



//缓存指定url返回的json

//解析文章详情 html

//下载html图片





//

@end
