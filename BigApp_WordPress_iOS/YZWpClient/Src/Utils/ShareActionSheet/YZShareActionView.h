//
//  YZShareActionView.h
//  YZWpClient
//
//  Created by zhoutl on 15/8/26.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface YZShareActionView : UIView<UICollectionViewDelegate, UICollectionViewDataSource>
{
    UIView *modalBackground;
}
@property (nonatomic, strong) NSMutableArray   *blocks;

@property (nonatomic, strong) UICollectionView *collectionView;

@property (nonatomic, strong) UIButton         *cancleButton;

@property (nonatomic, strong) UIView           *contentView;


@property (readonly,nonatomic, strong)NSMutableArray *imageNames ;
@property (readonly,nonatomic, strong)NSMutableArray *titles;

- (void)addIconWithTitle:(NSString *)title imageName:(NSString *)imageName block:(void (^)())block atIndex:(NSInteger)index;

- (void)showOnView:(UIView *)view;

- (void)dismissView;

@end
