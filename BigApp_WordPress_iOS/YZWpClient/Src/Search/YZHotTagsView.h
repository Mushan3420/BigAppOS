//
//  YZHotTagsView.h
//  YZWpClient
//
//  Created by zhoutl on 15/10/21.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol YZHotTagsViewDelegate <NSObject>

-(void)doSearchTagWithUrl:(NSString *)urlString;

@end

@interface YZHotTagsView : UIView<UIWebViewDelegate>
@property (nonatomic, strong)UIWebView *webView;

@property (nonatomic, strong)UILabel   *hotTagLable;
@property (nonatomic, strong)UILabel   *historyLable;

@property (nonatomic, strong) NSArray  *hotTagModels;

@property (nonatomic, weak) id<YZHotTagsViewDelegate> delegate;

@end


