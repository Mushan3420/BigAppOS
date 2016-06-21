//
//  YZHomeViewModel.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//


#import "YZHomeViewModel.h"
#import "BuildConfig.h"
#import "UIImageView+AFNetworking.h"

#import "UIImageView+PINRemoteImage.h"
#import "PINCache.h"
#import "FLAnimatedImageView.h"
#import "Config.h"

#define kMargin  10

#define kContentViewWidth0  (kSCREEN_WIDTH - 2 * kMargin)

#define kContentViewWidth1  (kSCREEN_WIDTH - 2 * kMargin - AutoDeviceWidth(230)/2.0 - 2)


#define kCellHeight0  (170/2.0)
#define kCellHeight1  AutoDeviceWidth(208/2.0)
#define kCellHeight2  (350/2.0)



@interface YZHomeViewModel()



@end

@implementation YZHomeViewModel
{
    
}
+ (instancetype)viewModel{
    YZHomeViewModel *vModel = [[YZHomeViewModel alloc] init];
    vModel.poastModel = [[YZPostsModel alloc] init];
    return vModel;
}

- (void)setPoastModel:(YZPostsModel *)poastModel{
    if (_poastModel != poastModel) {
        _poastModel = poastModel;
        
        [self setContentsFrames];
        [self setCommentTypes];
    }
}


- (void) setCommentTypes{
    if ([self.poastModel.comment_type isEqualToString:@"0"]) {
        _commentType = NotAllowCommentType;
    }
    else if ([self.poastModel.comment_type isEqualToString:@"1"]) {
        _commentType = AllowCommentNotLoginType;
        
    }else if ([self.poastModel.comment_type isEqualToString:@"2"]) {
        _commentType = NotAllowAnonymousCommentType;
    }
    else {
        _commentType = NeedLoninCommentType;
    }

}
- (NSMutableArray *)featuredImages{
    

    if (!_featuredImages) {
        _featuredImages = [@[] mutableCopy];

        [self.poastModel.featured_image enumerateObjectsUsingBlock:^(YZFeaturedImage *obj, NSUInteger idx, BOOL *stop) {
            
            UIImageView *imageView = [[UIImageView alloc] init];
            imageView.contentMode = UIViewContentModeScaleAspectFill;
            imageView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
            imageView.clipsToBounds = YES;
//            [imageView setImageWithURL:[NSURL URLWithString:obj.source]
//                      placeholderImage:[UIImage imageNamed:@"default_image"]];
               NSString *urlStr= [obj.source stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            
            if ([[Config getPictureQualityMod] isEqualToString:@"1"]) {
                urlStr = @"#";
            }
            
            [imageView pin_setImageFromURL:[NSURL URLWithString:urlStr]
                          placeholderImage:[UIImage imageNamed:@"defaultImage"]
                                completion:^(PINRemoteImageManagerResult *result) {
                                    if (result.requestDuration > 0.25) {
                                        [UIView animateWithDuration:0.3 animations:^{
                                            imageView.alpha = 1.0f;
                                        }];
                                    } else {
                                        imageView.alpha = 1.0f;
                                    }
                                }];
            
            [_featuredImages addObject:imageView];
            
        }];
    }
    
    return _featuredImages;
}


- (ImageCountsType)imageCountsType{

    switch (self.poastModel.featured_image.count) {
        case 0:
        {
            _imageCountsType =  NotImage;
        }
            break;
        case 1:
            _imageCountsType = OneImageType;
            break;
            
        default:
            _imageCountsType = MoreThanOneImageType;
            break;
    }
    
    return _imageCountsType;
}


- (void)setContentsFrames{

    switch (self.imageCountsType) {
        case NotImage:
        {
           _cellHeight = kCellHeight0;
            
            _imageListViewFrame    = CGRectZero;
            
            _articleDataLabelFrame = CGRectMake(kMargin,8, kContentViewWidth0, 22);

            _titleLabelFrame       = CGRectMake(kMargin, CGRectGetMaxY(_articleDataLabelFrame), kContentViewWidth0+4, 22);
            
            _abstractLabelFrame  = CGRectMake(kMargin, CGRectGetMaxY(_titleLabelFrame), kContentViewWidth0+6, 56/2.0);
            
        }
            break;
        case OneImageType:
        {
            _cellHeight = kCellHeight1;
            
            _imageListViewFrame    = CGRectMake((kMargin), kMargin, AutoDeviceWidth(230/2.0), AutoDeviceWidth(160)/2.0);
            
            _titleLabelFrame       = CGRectMake(CGRectGetMaxX(_imageListViewFrame) + 8,
                                                kMargin+14,
                                                kContentViewWidth1,
                                                40);
            
            _abstractLabelFrame  = CGRectMake(CGRectGetMaxX(_imageListViewFrame) + 8,
                                              CGRectGetMaxY(_titleLabelFrame)+AutoDeviceWidth(0),
                                              kContentViewWidth1,
                                              20);
            
            _articleDataLabelFrame = CGRectMake(CGRectGetMaxX(_imageListViewFrame) + 8,
                                                4,
                                                50,
                                                22);
        }
            break;
        case MoreThanOneImageType:
        {
            _cellHeight = kCellHeight2;
            
            _abstractLabelFrame = CGRectZero;
            
            _articleDataLabelFrame = CGRectMake(kMargin,6, kContentViewWidth0, 20);

            _titleLabelFrame       = CGRectMake(kMargin, CGRectGetMaxY(_articleDataLabelFrame), kContentViewWidth0, 22);
            
            _imageListViewFrame    = CGRectMake(kMargin, CGRectGetMaxY(_titleLabelFrame)+8, kContentViewWidth0, 111);

        }
            break;
            
        default:
            _cellHeight = kCellHeight0;
            break;
    }
    
    
}



- (NSString *)titleText{
    if (_titleText) {
        return _titleText;
    }
    return self.poastModel.title;
}

-(NSString *)commentsText{
    return [NSString stringWithFormat:@"%@%ld.%@%ld.",NSLocalizedString(@"CELL_READ_NUM", nil),self.poastModel.views,NSLocalizedString(@"CELL_COMMENT_NUM", nil),self.poastModel.comment_num];
}
-(NSString *)articleDataText{
    NSArray  *array = [_poastModel.date_gmt componentsSeparatedByString:@"T"];
    NSArray *array2 = [array.firstObject componentsSeparatedByString:@"-"];
    return [NSString stringWithFormat:@"%@-%@",array2[1],array2.lastObject];
}

-(NSMutableArray *)imageList{
    NSMutableArray *list = [@[] mutableCopy];
    
    [self.poastModel.featured_image enumerateObjectsUsingBlock:^(YZFeaturedImage *obj, NSUInteger idx, BOOL *stop) {
        [list addObject:obj.source];
        
    }];
    
    return list;
}

- (NSString *)flattenHTML:(NSString *)html {
    
    NSScanner *theScanner;
    NSString *text = nil;
    
    theScanner = [NSScanner scannerWithString:html];
    
    while ([theScanner isAtEnd] == NO) {

        [theScanner scanUpToString:@"<" intoString:NULL] ;

        [theScanner scanUpToString:@">" intoString:&text] ;


        html = [html stringByReplacingOccurrencesOfString:
                [NSString stringWithFormat:@"%@>", text]
                                               withString:@""];
    }
    return html;
}//过滤html标签



@end
