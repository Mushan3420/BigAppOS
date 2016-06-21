//
//  YZHotTagsView.m
//  YZWpClient
//
//  Created by zhoutl on 15/10/21.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZHotTagsView.h"
#import "BuildConfig.h"
#import "UIColor+SNAdditions.h"
#import "UIViewExt.h"
#import "YZPostsModel.h"


@implementation YZHotTagsView

- (instancetype)initWithFrame:(CGRect)frame{
        self = [super initWithFrame:frame];
    if (self) {
        
      
        
        self.frame = CGRectMake(0, 0, kSCREEN_WIDTH, (56*2 + 180)/2.0);
        
        self.backgroundColor = [UIColor colorWithHexString:@"fafafa"];
        
//        [self loadLocalHtml];
        


    }
    return self;
}

-(UILabel *)hotTagLable{
    if (!_hotTagLable) {
        _hotTagLable = [[UILabel alloc] init];
        _hotTagLable.frame = CGRectMake(0, 0, kSCREEN_WIDTH, 56/2.0);
        _hotTagLable.backgroundColor = [UIColor clearColor];
        _hotTagLable.text =@"   热门标签";
        _hotTagLable.font = [UIFont fontWithName:kChineseFontNameXi size:12];
        _hotTagLable.textColor = RGBCOLOR(153, 153, 153);
        
        _hotTagLable.layer.borderWidth = 0.5;  // 给图层添加一个有色边框
        _hotTagLable.layer.borderColor = [UIColor colorWithHexString:@"d5d5d5"].CGColor;

    }
    return _hotTagLable;
}

- (UILabel *)historyLable{
    if (!_historyLable) {
        _historyLable = [[UILabel alloc] init];
        _historyLable.backgroundColor = [UIColor clearColor];
        _historyLable.text =@"   搜索历史";
        _historyLable.frame = CGRectMake(0,0, kSCREEN_WIDTH, 56/2.0);

        _historyLable.font = [UIFont fontWithName:kChineseFontNameXi size:12];
        _historyLable.textColor = RGBCOLOR(153, 153, 153);
        _historyLable.layer.borderWidth = 0.5;
        _historyLable.layer.borderColor = [UIColor colorWithHexString:@"d5d5d5"].CGColor;

    }
    
    return _historyLable;
}


- (UIWebView *)webView{
    if (!_webView) {
        _webView = [[UIWebView alloc] init];
        
        _webView.frame = CGRectMake(-1, 56/2.0, kSCREEN_WIDTH+2, (180)/2.0);
        
        _webView.scrollView.bounces = NO;

        _webView.delegate = self;
    }
    
    return _webView;
}


- (void)setHotTagModels:(NSArray *)hotTagModels{
    
    __block  NSString *elements = @"";
    _hotTagModels = hotTagModels;
    
    [_hotTagModels enumerateObjectsUsingBlock:^(YZPostTag *obj, NSUInteger idx, BOOL *stop) {
        elements = [NSString stringWithFormat:@"%@<a href ='%@'>%@</a>",elements,obj.link,obj.name];
    }];
    
    [self loadLocalHtml:elements];
}

- (void)loadLocalHtml:(NSString *)elements{
    
    NSString *style = @"<style type='text/css'> a{font-family: STHeitiSC-LightXi;float:left;border-radius: 5px;-webkit-border-radius: 5px;font-size:12px;margin:5px 4px 6px 8px;background-color:#F7F7F7;padding:6px 16px 6px 16px;color: rgb(48, 48, 48);-webkit-user-select: display;-webkit-touch-callout: display;-webkit-tap-highlight-color :color; text-decoration:none;}</style>";

    NSString *htmlString = [NSString stringWithFormat:@"%@%@",style,elements];
    
    [self.webView loadHTMLString:htmlString baseURL:nil];
}

#pragma mark -  Webview Delegate
-(BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType{
    
    NSString *urlStr=request.URL.absoluteString;
    
    NSLog(@"url: %@",urlStr);
    
    //为空，第一次加载本页面
    if ([urlStr isEqualToString:@"about:blank"]) {
        return YES;
    }
    
    
    if (self.delegate && [self.delegate respondsToSelector:@selector(doSearchTagWithUrl:)])
    {
        [self.delegate doSearchTagWithUrl:urlStr];
    }

    
 
    
    
    
    return  NO;
}


- (void)layoutSubviews{
      [self addSubview:self.webView];
    [self addSubview:self.hotTagLable];
    [self addSubview:self.historyLable];
    
    if (_hotTagModels.count>0) {
        _historyLable.frame = CGRectMake(0,self.webView.bottom, kSCREEN_WIDTH, 56/2.0);

    }

}
@end
