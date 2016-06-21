//
//  FontSetSheet.h
//  YZWpClient
//
//  Created by zhoutl on 15/8/11.
//  Copyright (c) 2015年 com.youzu. All rights reserved.

//

#import <UIKit/UIKit.h>
#import "FontSetSheetCell.h"
@protocol FontSetSheetDelegate <NSObject>
@optional
-(void)didSelectIndex:(NSInteger)index;
@end

@interface FontSetSheet : UIView<UITableViewDataSource,UITableViewDelegate,UIGestureRecognizerDelegate>{
    NSArray *listData;

}
@property(nonatomic, assign)BOOL isShow;
-(id)initWithlist:(NSArray *)list height:(CGFloat)height;
- (void)showInView:(UIViewController *)Sview;
@property(nonatomic,assign) id <FontSetSheetDelegate> delegate;
@property(nonatomic,strong) UIButton *cancleButton;
@property(nonatomic,strong) UITableView *view;
@end


