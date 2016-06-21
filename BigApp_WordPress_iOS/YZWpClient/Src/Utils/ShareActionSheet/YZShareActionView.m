//
//  YZShareActionView.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/26.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#define kShareCellHeight floor(AutoDeviceWidth(216)/2.0)
#define kShareCellWidth ([UIScreen mainScreen].bounds.size.width/3.0)
#define kShareActionViewWith  ([UIScreen mainScreen].bounds.size.width)

#define kSharecolumns 3
#define kSharerows 2

#import "YZShareActionView.h"
#import "YZShareActionCell.h"
#import "BuildConfig.h"
#import "UIViewExt.h"

@implementation YZShareActionView
{
    NSInteger _sharerows;
}
- (void)dealloc
{
    _blocks = nil;
    _titles = nil;
    _imageNames = nil;
}
static NSString *cellIdentifier = @"YZShareActionCell";
-(instancetype)init{
    if (self = [super init]) {
          _blocks = [[NSMutableArray alloc] init];
          _titles = [[NSMutableArray alloc] init];
          _imageNames = [[NSMutableArray alloc] init];
    }
    return self;
}

#pragma mark-- Methods
- (void)addIconWithTitle:(NSString *)title imageName:(NSString *)imageName block:(void (^)())block atIndex:(NSInteger)index{
    
    if (index >= 0)
    {
        [self.blocks insertObject:block ? [block copy] : [NSNull null]
                          atIndex:index];
        [_titles insertObject:title atIndex:index];
        [_imageNames insertObject:imageName atIndex:index];
        
    }
    else
    {
        [self.blocks addObject:block ? [block copy] : [NSNull null]];
        [_titles addObject:title];
        [_imageNames addObject:imageName];
    }

    
}

- (void)showOnView:(UIView *)parentView{
    
    if (self.blocks.count >3) {
        _sharerows = 2;
    }
    else{
        _sharerows = 1;
    }
    
    self.frame = [UIScreen mainScreen].bounds;
    _contentView = [[UIView alloc] initWithFrame:self.bounds];
    _contentView.backgroundColor = [UIColor clearColor];
    _contentView.userInteractionEnabled = YES;
    self.backgroundColor = RGBACOLOR(0, 0, 0, 0);
    
    modalBackground = [[UIView alloc] initWithFrame:self.bounds];
    modalBackground.backgroundColor = [UIColor clearColor];
    [self insertSubview:modalBackground atIndex:0];
    
    UITapGestureRecognizer *tapGesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissView)];
    [modalBackground addGestureRecognizer:tapGesture];

    [_contentView addSubview:self.collectionView];
    [_contentView addSubview:self.cancleButton];
    [self addSubview:_contentView];
    
    _contentView.height = kShareCellHeight * _sharerows + self.cancleButton.height;
    
    [parentView addSubview:self];
    
    CGRect frame = _contentView.frame;
    frame.origin.y = parentView.frame.size.height;
    frame.size.height = _contentView.height;
    _contentView.frame = frame;
    __block CGPoint center = _contentView.center;
    center.y -= _contentView.height;
    [UIView animateWithDuration:0.28
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseInOut
                     animations:^{
                         self.backgroundColor = RGBACOLOR(0, 0, 0, 0.45);
                         _contentView.center = center;
                             
                     } completion:^(BOOL finished) {
                         _contentView.center = center;
                     }];

}

- (void)dismissView{

    CGPoint center = _contentView.center;
    center.y += _contentView.bounds.size.height;
    [UIView animateWithDuration:0.28
                          delay:0.0
                        options:UIViewAnimationOptionCurveEaseInOut
                     animations:^{
                         _contentView.center = center;
                         self.alpha = 0.0;
                     } completion:^(BOOL finished) {
                         [self removeFromSuperview];
                     }];
}


#pragma mark -- Propertys
- (UICollectionView *)collectionView{
    if (!_collectionView) {
        
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        [flowLayout setScrollDirection:UICollectionViewScrollDirectionVertical];
        [flowLayout setItemSize:CGSizeMake(kShareCellWidth, kShareCellHeight)];
        [flowLayout setMinimumInteritemSpacing:0];
        [flowLayout setMinimumLineSpacing:0];
        
        NSInteger collectionHeight = (kShareCellHeight*_sharerows);
        
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectMake(0, 0,
                                                                             kShareActionViewWith,
                                                                             collectionHeight)
                                             collectionViewLayout:flowLayout];
        _collectionView.dataSource=self;
        _collectionView.delegate=self;
        [_collectionView setBounces:NO];
        [_collectionView registerClass:[YZShareActionCell class] forCellWithReuseIdentifier:cellIdentifier];
        _collectionView.backgroundColor = [UIColor whiteColor];
 
    }
    
    return _collectionView;
}

- (UIButton *)cancleButton{
    if (!_cancleButton) {
        _cancleButton= [UIButton buttonWithType:UIButtonTypeCustom];
        _cancleButton.titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:14];
        _cancleButton.frame = CGRectMake(0, self.collectionView.height, kSCREEN_WIDTH, 47);
        [_cancleButton setTitle:LocalizedString(@"CANCEL_BTN") forState:UIControlStateNormal];
        [_cancleButton setTitleColor:RGBCOLOR(48, 48, 48) forState:UIControlStateNormal];
        [_cancleButton addTarget:self action:@selector(dismissView) forControlEvents:UIControlEventTouchUpInside];
        _contentView.backgroundColor = [UIColor whiteColor];
    }
    return _cancleButton;
    
}

#pragma mark - View Collection Methods

-(NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}

-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.blocks.count;
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    
    YZShareActionCell *cell = [self.collectionView dequeueReusableCellWithReuseIdentifier:cellIdentifier forIndexPath:indexPath];
    cell.titleView.text = _titles[indexPath.row];
    cell.imageView.image = [UIImage imageNamed:_imageNames[indexPath.row]];
    
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    id obj = [self.blocks objectAtIndex:indexPath.row];
    if (![obj isEqual:[NSNull null]])
    {
        ((void (^)())obj)();
    }
}



@end
