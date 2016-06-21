//
//  FontSetSheet.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/11.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "FontSetSheet.h"
#import "UIImage+SNAdditions.h"
#import "BuildConfig.h"

@implementation FontSetSheet
{
    NSInteger _height;
}
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
    }
    return self;
}

-(id)initWithlist:(NSArray *)list height:(CGFloat)height{
    self = [super init];
    if(self){
        self.frame = CGRectMake(0, 0, ScreenWidth, ScreenHeight);
        self.backgroundColor = RGBACOLOR(0, 0, 0, 0);
        
        
        _height =  (list.count *44 > ScreenHeight/2 ) ? ScreenHeight/2:list.count *44 ;
        _view = [[UITableView alloc]initWithFrame:CGRectMake(0, ScreenHeight, ScreenWidth,height+60) style:UITableViewStylePlain];
        [_view setSeparatorStyle:UITableViewCellSeparatorStyleNone];
        _view.bounces = NO;
        _view.backgroundColor = [UIColor whiteColor];

        _view.dataSource = self;
        _view.delegate = self;
        listData = list;
        _view.scrollEnabled = YES;
        
        YZView *footview = [YZView new];
        footview.frame = CGRectMake(0, 0, 0, 60);
        [footview addSubview:self.cancleButton];
        [footview setThemeViewBackgroudColor:kTmViewBackgroudColor];
        [footview openThemeSkin];
        
        
        _view.tableFooterView = footview;
        [self addSubview:_view];

    }
    return self;
}

-(void)animeData{
    //self.userInteractionEnabled = YES;
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tappedCancel)];
    [self addGestureRecognizer:tapGesture];
    tapGesture.delegate = self;
        self.alpha = 1.0;
    _view.frame = CGRectMake(0, ScreenHeight, ScreenWidth,_height+60);
    [UIView animateWithDuration:.25 animations:^{
        self.backgroundColor = RGBACOLOR(0, 0, 0, .45);
        [UIView animateWithDuration:.25 animations:^{
            [_view setFrame:CGRectMake(_view.frame.origin.x, ScreenHeight-_view.frame.size.height, _view.frame.size.width, _view.frame.size.height)];
        }];
    } completion:^(BOOL finished) {
    }];
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch{
    if([touch.view isKindOfClass:[self class]]){
        return YES;
    }
    return NO;
}

-(void)tappedCancel{
    [UIView animateWithDuration:.25 animations:^{
        [_view setFrame:CGRectMake(0, ScreenHeight,ScreenWidth, 0)];
        self.alpha = 0;
    } completion:^(BOOL finished) {
        if (finished) {
            [self removeFromSuperview];
            _isShow = NO;
        }
    }];
}

- (void)showInView:(UIViewController *)Sview
{
    _isShow = YES;
    if(Sview==nil){
        [[UIApplication sharedApplication].delegate.window.rootViewController.view addSubview:self];
    }else{
    //[view addSubview:self];
        [Sview.view addSubview:self];
        
    }
    [self animeData];
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [listData count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    FontSetSheetCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if(cell==nil){
        cell = [[FontSetSheetCell alloc]initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        [cell.segmentControl addTarget:self action:@selector(change:) forControlEvents:UIControlEventValueChanged];
    }
    [cell setData:[listData objectAtIndex:indexPath.row]];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [self tappedCancel];
    if(_delegate!=nil && [_delegate respondsToSelector:@selector(didSelectIndex:)]){
        [_delegate didSelectIndex:indexPath.row];
        return;
    }
}

-(void)change:(UISegmentedControl *)seg{
    if(_delegate!=nil && [_delegate respondsToSelector:@selector(didSelectIndex:)]){
        [_delegate didSelectIndex:seg.selectedSegmentIndex];

    }
}

- (void)didCancleAction:(id)sender{
    [self tappedCancel];}

- (UIButton *)cancleButton{
    if (!_cancleButton) {
        
        _cancleButton= [YZButton buttonWithType:UIButtonTypeCustom];
        _cancleButton.alpha = 0.95;
        _cancleButton.titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:16];
        _cancleButton.frame = CGRectMake(5, 10, kSCREEN_WIDTH-10, 40);
        [_cancleButton setTitle:LocalizedString(@"FINISH_BTN") forState:UIControlStateNormal];
//        _cancleButton.contentEdgeInsets = UIEdgeInsetsMake(10, 0, 0, 0);
        [_cancleButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        
        [_cancleButton addTarget:self action:@selector(didCancleAction:) forControlEvents:UIControlEventTouchUpInside];
        _cancleButton.layer.borderWidth = 0.5;
        _cancleButton.layer.borderColor = [RGBCOLOR(218, 218, 218) CGColor];
        
        [(YZButton *)_cancleButton setThemeButtonBackgroudColor:kTmClearColor];
        [(YZButton *)_cancleButton setthemeTextColor:kTmDetailCellTextColor];
        [(YZButton *)_cancleButton openThemeSkin];
    }
    return _cancleButton;
}


@end

