//
//	YZNavListModel.m
//
//	Create by 桃林 周 on 22/7/2015
//	Copyright © 2015. All rights reserved.



#import "YZNavListModel.h"
#import "MJExtension.h"
@interface YZNavListModel ()
@end
@implementation YZNavListModel

- (void)setBanner_list:(NSArray *)bannerList{
    _banner_list = [YZBannerList objectArrayWithKeyValuesArray:bannerList];
}

@end



@interface YZBannerList ()
@end
@implementation YZBannerList

@end