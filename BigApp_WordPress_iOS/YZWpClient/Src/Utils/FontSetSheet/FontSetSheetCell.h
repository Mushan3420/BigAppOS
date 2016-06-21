//
//  FontSetSheetCell.h
//  YZWpClient
//
//  Created by zhoutl on 15/8/11.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>

//获取设备的物理高度
#define ScreenHeight [UIScreen mainScreen].bounds.size.height
//获取设备的物理宽度
#define ScreenWidth [UIScreen mainScreen].bounds.size.width
#import "FontSetSheetModel.h"
@interface FontSetSheetCell : UITableViewCell{
    UIImageView *leftView;
    UILabel *InfoLabel;
    FontSetSheetModel *cellData;
    UIView *backgroundView;
}
@property(nonatomic)BOOL hadObserver;
@property (nonatomic, strong) UISegmentedControl *segmentControl;
-(void)setData:(FontSetSheetModel *)dicdata;
@end
