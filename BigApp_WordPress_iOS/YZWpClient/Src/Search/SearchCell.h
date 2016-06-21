//
//  SearchCell.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/8.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "YZTableViewCell.h"

#define kSearchHistoryCellRow 44

@interface SearchCell : YZTableViewCell

@property(strong,nonatomic)void(^searchTitleBlock)(NSString *);

@end
