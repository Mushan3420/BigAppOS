//
//  YZLinkViewController.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/31.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZLinkViewController.h"
#import "UIViewExt.h"
@interface YZLinkViewController ()<UIWebViewDelegate>
{
    UIWebView *_webView;
}
@end

@implementation YZLinkViewController
- (void)dealloc
{
    _webView.delegate = nil;
     [_webView stopLoading];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    _webView = [[UIWebView alloc] initWithFrame:self.view.bounds];
        _webView.delegate = self;
    NSURLRequest *request =[NSURLRequest requestWithURL:[NSURL URLWithString:self.urlString]];
    [_webView loadRequest:request];
    _webView.backgroundColor = [UIColor whiteColor];
    self.tableView.tableHeaderView = _webView;

    self.isBackBarButtonItemShow = YES;

    self.tableView.tableFooterView = [UIView new];
    self.view.backgroundColor = [UIColor whiteColor];
    _webView.width = kSCREEN_WIDTH;
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    [_webView stopLoading];
}

#pragma mark -  Webview Delegate
-(void)webViewDidFinishLoad:(UIWebView *)webView{
    
    webView.height = [webView.scrollView contentSize].height;
    
    [webView.scrollView.panGestureRecognizer requireGestureRecognizerToFail:self.tableView.panGestureRecognizer];
    
    self.tableView.tableHeaderView = _webView;

    
}

@end
