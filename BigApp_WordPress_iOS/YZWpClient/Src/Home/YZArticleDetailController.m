//
//  YZArticleDetailController.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/20.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZArticleDetailController.h"
#import "YZHomeService.h"
#import "YZArticelDetailModel.h"
#import "UIViewExt.h"
#import "UIViewController+YZProgressHUD.h"
#import "YZDetailCommentCell.h"
#import "YZArticleDetailBottomBar.h"
#import "YZDetailCommentViewModel.h"
#import "YZWriteCommentView.h"
#import "Config.h"
#import "YZCommentController.h"
#import "YZLoginController.h"
#import "YZLinkViewController.h"
#import "YZFavService.h"
#import "FontSetSheet.h"
#import "YZShareActionView.h"
#import <ShareSDK/ShareSDK.h>
#import <ShareSDKExtension/ShareSDK+Extension.h>
#import "AppConfigManager.h"
#import "NSString+Additions.h"
#import "PINCache.h"
#import "UIImage+SNAdditions.h"
#import "WebViewJavascriptBridge.h"
#import "YZQueryComponents.h"

#import "SDWebImageManager.h"

#import "YZHistoryDBModel.h"
#import "YZSavePostDBModel.h"
#import "YZHomeController.h"
#import "YZNavListModel.h"
//#import "ThemeManager.h"

#define kYZDetailCommentCell   @"YZDetailCommentCell"
#define kSectionHeigh          38

@interface YZArticleDetailController ()<UIWebViewDelegate,FontSetSheetDelegate>

@property (nonatomic, strong) UIWebView                *articleDetailView;

@property (nonatomic, strong) YZArticleDetailBottomBar *bottomBar;

@property (nonatomic, strong) UIView                   *footerView;

@property (nonatomic, strong) NSMutableArray           *commentsDataArray;

@property (nonatomic, strong) YZWriteCommentView       *writeCommentView;

@property (nonatomic, strong) YZDetailCommentViewModel *lastOpenCommentModel;

@property (nonatomic, strong) UIButton *moreCommentsBtn;

@property (nonatomic, assign) NSInteger    currentPage;

@property (nonatomic, strong) YZArticelDetailModel *detailModel;

@property (nonatomic, strong) UIButton *fontSetButton;

@property (nonatomic, strong) FontSetSheet *fontSetSheet;

@property (nonatomic, strong) WebViewJavascriptBridge *jsBridge;

@property (nonatomic, strong) NSMutableArray *contentImages;//存放html中所有图片

@property (nonatomic, strong) YZSavePostDBModel *saveModel;


@end

@implementation YZArticleDetailController
- (void)dealloc
{
    _articleDetailView.delegate = nil;
    
    [_articleDetailView stopLoading];
    _articleDetailView = nil;
    [_articleDetailView loadHTMLString:@"" baseURL:nil];

    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
}

- (instancetype)initWithViewModel:(YZHomeViewModel *)viewModel{
    if (self = [super init]) {
        _viewModel = viewModel;
        
        YZHistory *history = [[YZHistory alloc] init];
        
        history.ID = [NSString stringWithFormat:@"%@",@(_viewModel.poastModel.postID)];
        history.historyTitle = _viewModel.poastModel.title;
        
        YZHistoryDBModel *db = [[YZHistoryDBModel alloc]init];
        [db saveAhistory:history];
    }
    
    return self;
}

- (instancetype)initWithUrlString:(NSString *)urlString{
    if (self = [super init]) {
        _urlString = urlString;
        
        NSURL *url = [NSURL URLWithString:urlString];
        
        NSMutableDictionary *params = [url queryComponents];
        
        DLog(@"queryComponents: %@",params);
        
        
        NSArray *idS = params[@"id"];
        if (idS) {
            _viewModel = [[YZHomeViewModel alloc] init];
            _viewModel.poastModel = [[YZPostsModel alloc] init];
            
            _viewModel.poastModel.postID = [[idS firstObject] integerValue];
        }

    }
    
    return self;
}

- (void)configureViews
{
    NSDictionary *attributes=[NSDictionary dictionaryWithObjectsAndKeys:[[ThemeManager sharedInstance] colorWithColorName:kTmNavTitleColor],NSForegroundColorAttributeName,nil];
    
    [[UINavigationBar appearance] setBarTintColor:[[ThemeManager sharedInstance] colorWithColorName:kTmNavBarColor]];
    [[UINavigationBar appearance] setTintColor:[[ThemeManager sharedInstance] colorWithColorName:kTmNavTintColor]];
    [[UINavigationBar appearance]setTitleTextAttributes:attributes];
    [[UIToolbar appearance]setBarTintColor:[[ThemeManager sharedInstance] colorWithColorName:kTmToolBarColor]];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setupJSBridge];
    
    _currentPage = 1;
    self.saveModel = [[YZSavePostDBModel alloc]init];
    
    _commentsDataArray = [@[] mutableCopy];
    
    self.isBackBarButtonItemShow = YES;
    
     self.tableView.tableHeaderView = self.articleDetailView;

    YZView *vi = [[YZView alloc]init];
    [vi setThemeViewBackgroudColor:kTmViewBackgroudColor];
    [vi openThemeSkin];
    self.tableView.backgroundView = vi;
    
    
    [self.tableView registerClass:[YZDetailCommentCell class] forCellReuseIdentifier:kYZDetailCommentCell];
    
    [self.tableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    
//    [self showLoadingAddedTo:self.view];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyBoardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyBoardWillHide:) name:UIKeyboardWillHideNotification object:nil];
    [self configureViews];
//    [self regitserAsObserver];
    
//    if (self.urlString.length > 0) {
//        [self loadRemoteUrl];
//    }else{
        //缓存
        NSString *cacheKey = [[NSString stringWithFormat:@"%@-%@-%@",@(_viewModel.poastModel.postID),_viewModel.poastModel.modified,[Config getPictureQualityMod]] md5Hash];
        
        id object = [[PINCache sharedCache] objectForKey:cacheKey];
        
        if (!object || !_viewModel.poastModel) {
            [self loadDetailData:YES];
        }else{
            YZArticelDetailModel *detailModel = [YZArticelDetailModel objectWithKeyValues:object];
            self.detailModel = detailModel;
            [self refreshUI];
            
        }
       

//    }
    
    UIBarButtonItem *fontSetItem = [[UIBarButtonItem alloc] initWithCustomView:self.fontSetButton];
    self.navigationItem.rightBarButtonItem = fontSetItem;
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];

    if (self.navigationController.viewControllers.count > 1) {
        [self.navigationController.view addSubview:self.bottomBar];
    }
    
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    
    [self.bottomBar removeFromSuperview];
    
    [self.writeCommentView removeFromSuperview];
}

#pragma mark -- Propertys
-(FontSetSheet *)fontSetSheet{
    if (!_fontSetSheet) {
        FontSetSheetModel *sheetModel = [[FontSetSheetModel alloc]init];
        NSArray *menuList = [[NSArray alloc]init];
        
        menuList = @[sheetModel];
        _fontSetSheet = [[FontSetSheet alloc] initWithlist:menuList height:0];
        _fontSetSheet.delegate = self;
    }
    
    return _fontSetSheet;
}


-(UIButton *)fontSetButton{
    if (!_fontSetButton) {
        UIButton *button = [YZButton buttonWithType:UIButtonTypeCustom];

        [button setImage:[UIImage imageNamed:@"font_set_button"] forState:UIControlStateNormal];
        [button setImage:[UIImage imageNamed:@"font_set_button"] forState:UIControlStateHighlighted];
        button.frame = CGRectMake(0, 0, AutoDeviceWidth(57.0/2),AutoDeviceWidth(57/2.0));
//        button.contentEdgeInsets = UIEdgeInsetsMake(0, -15, 0, 0);
        [button addTarget:self action:@selector(showFontSettingView) forControlEvents:UIControlEventTouchUpInside];

        YZButton *but = (YZButton *)button;
        [but setThemeButtonBackgroudColor:kTmClearColor];
        [but setThemeButtonImage:@"font_set_button@2x"];
        [but openThemeSkin];
        
        _fontSetButton = button;
        

    }
    return _fontSetButton;
}

-(YZWriteCommentView *)writeCommentView{
    if (!_writeCommentView) {
        _writeCommentView = [[YZWriteCommentView alloc] init];
        [_writeCommentView.closeBtn addTarget:self action:@selector(endEditing:) forControlEvents:UIControlEventTouchUpInside];
        [_writeCommentView.sendCommentBtn addTarget:self action:@selector(sendCommentAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _writeCommentView;
}

-(UIWebView *)articleDetailView
{
    if (!_articleDetailView) {
        _articleDetailView = [[YZWebView alloc] init];
        _articleDetailView.frame =CGRectMake(0, 0, kSCREEN_WIDTH, kSCREEN_HEIGHT);
        _articleDetailView.delegate = self;
        _articleDetailView.contentMode = UIViewContentModeScaleAspectFit;
        _articleDetailView.opaque = NO;

        YZWebView *web = (YZWebView *)_articleDetailView;
        [web setThemeWebBackgroundColor:kTmDetailWebViewBackgroudColor];
        [web setThemeWebTextColor:kTmDetailWebViewTextColor];
        [web openThemeSkin];
        web.backgroundColor = [[ThemeManager sharedInstance] colorWithColorName:kTmDetailWebViewBackgroudColor];
    }
    
    return _articleDetailView;
}

-(YZArticleDetailBottomBar *)bottomBar{
    
    if (!_bottomBar) {
        
        CGRect frame=CGRectMake(0, kSCREEN_HEIGHT - 50, kSCREEN_WIDTH, 50);
        _bottomBar = [[YZArticleDetailBottomBar alloc] initWithFrame:frame];
        [_bottomBar.sendCommentBtn addTarget:self action:@selector(showWriteCommentAction:) forControlEvents:UIControlEventTouchUpInside];
        [_bottomBar.commentLocateBtn addTarget:self action:@selector(locateToCommentAction:) forControlEvents:UIControlEventTouchUpInside];
        [_bottomBar.favBtn addTarget:self action:@selector(favBtnAction:) forControlEvents:UIControlEventTouchUpInside];
        [_bottomBar.shareBtn addTarget:self action:@selector(showShareViewAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return _bottomBar;
}

- (UIView *)footerView{
    if (!_footerView) {
        _footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kSCREEN_WIDTH, 100)];
        
        if (!_moreCommentsBtn) {
            _moreCommentsBtn = [YZButton buttonWithType:UIButtonTypeCustom];
            [_moreCommentsBtn setTitle:@"加载中..." forState:UIControlStateNormal];
            [_moreCommentsBtn setTitleColor:RGBCOLOR(102, 102, 102) forState:UIControlStateNormal];
            
            _moreCommentsBtn.titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:14];
            _moreCommentsBtn.frame = CGRectMake(0, 0, _footerView.width, 50);
            [_moreCommentsBtn addTarget:self action:@selector(loadCommentsData) forControlEvents:UIControlEventTouchUpInside];
            
            YZButton *but = (YZButton *)_moreCommentsBtn;
            [but setThemeButtonBackgroudColor:kTmDetailSectionBackgroudColor];
            [but setthemeTextColor:kTmDetailSectionTextColor];
            [but openThemeSkin];
        }

        [_footerView addSubview:_moreCommentsBtn];
        
        YZView *lineTop = [[YZView alloc] init];
        lineTop.backgroundColor = RGBCOLOR(210, 210, 210);
        lineTop.frame = CGRectMake(0, 0, kSCREEN_WIDTH, 0.5);
        [_footerView addSubview:lineTop];
        
        [lineTop setThemeViewBackgroudColor:kTmDetailCellSeperColor];
        [lineTop openThemeSkin];
        
        
        


    }
    
    return _footerView;
}



#pragma mark -  Webview Delegate
-(BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType{
    
    NSString *urlStr=request.URL.absoluteString;
    
    NSLog(@"url: %@",urlStr);
    
    //为空，第一次加载本页面
    if ([urlStr isEqualToString:@"about:blank"] || [urlStr hasPrefix:@"file://"]) {
        return YES;
    }
    
    //点击标签 处理
    if ([urlStr rangeOfString:@"get_posts&filter%5Btag%5D"].location != NSNotFound) {
        YZHomeController *viewController = [[YZHomeController alloc] init];
        viewController.categoryModel = [[YZNavListModel alloc] init];
        viewController.categoryModel.link = urlStr;
        
        NSURL *url = [NSURL URLWithString:urlStr];
        NSMutableDictionary *params = [url queryComponents];
        
        NSArray *tagName = params[@"filter[tag]"];
        viewController.title = tagName.firstObject;
        
        viewController.isBackBarButtonItemShow = YES;
        
        [self.navigationController pushViewController:viewController animated:YES];

    }
    else{
        //设置点击后的视图控制器
        YZLinkViewController *originalC=[[YZLinkViewController alloc] init];
        originalC.urlString=urlStr;
        [self.navigationController pushViewController:originalC animated:YES];
    }
    

    
    return  NO;
}

-(void)webViewDidFinishLoad:(UIWebView *)webView{
    
//    [self dismissHuDForView:self.view];
    
    [self didSelectIndex:-100];


    
    [self.articleDetailView.scrollView.panGestureRecognizer requireGestureRecognizerToFail:self.tableView.panGestureRecognizer ];
    
    self.tableView.tableHeaderView = self.articleDetailView;
    self.tableView.tableFooterView = self.footerView;
    
    if (_commentsDataArray.count==0) {
         [self loadCommentsData];
    }
    
    
    
}


#pragma mark -- UITableViewDelegate UITableViewDataSource

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    return kSectionHeigh;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    UILabel *headerLabel = [[YZLabel alloc] init];
    headerLabel.frame = CGRectMake(0, 0, kSCREEN_HEIGHT- 20*2, kSectionHeigh);
    headerLabel.text = [NSString stringWithFormat:@"     %@",LocalizedString(@"LATEST_COMMENT")];
    headerLabel.font = [UIFont fontWithName:kChineseFontNameXi size:14];
//    headerLabel.backgroundColor = [UIColor whiteColor];
    
    [(YZLabel *)headerLabel setThemeLabBackgroundColor:kTmDetailSectionBackgroudColor];
    [(YZLabel *)headerLabel setThemeTextColor:kTmDetailSectionTextColor];
    [(YZLabel *)headerLabel openThemeSkin];

    UIView *lineTop = [[YZView alloc] init];
    lineTop.backgroundColor = RGBCOLOR(210, 210, 210);
    lineTop.frame = CGRectMake(0, 0, kSCREEN_WIDTH, 0.5);
    [headerLabel addSubview:lineTop];
    
    [(YZView *)lineTop setThemeViewBackgroudColor:kTmDetailCellSeperColor];
    [(YZView *)lineTop openThemeSkin];
    
    return headerLabel;
}



- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.commentsDataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    YZDetailCommentViewModel *commentViewModel = self.commentsDataArray[indexPath.row];
    
    return commentViewModel.cellHeight;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    YZDetailCommentCell *cell = [tableView dequeueReusableCellWithIdentifier:kYZDetailCommentCell forIndexPath:indexPath];
    
    cell.viewModel = self.commentsDataArray[indexPath.row];
    
    return cell;
}


-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{

    [self reloadRowsAtIndexPath:indexPath];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


#pragma mark --  Methods

- (void)refreshUI{

    [self loadLocalHTMLString];
    
    if (![Config isLogin]) {
        _detailModel.is_favorited = [self.saveModel isExistApostModel:self.viewModel.poastModel.postID];
        
    }
    self.bottomBar.favBtn.selected = _detailModel.is_favorited;
   
}

- (void)loadLocalHTMLString{
    NSString *htmlStart = [self parserData:@"detail.html"];

    
    NSString *titleString = [NSString stringWithFormat:@"<h1>%@</h1>" ,_detailModel.title];
    
    NSString *dataString = @"";

    if (_viewModel.poastModel.date) {
        dataString = _viewModel.poastModel.date;
    }
    else{
        dataString = _detailModel.date;
    }
    
    NSArray  *array = [dataString componentsSeparatedByString:@"T"];
    NSString *date = [NSString stringWithFormat:@"<p class = pcolor> %@<p>",array.firstObject];
    
    NSString *htmlEndString = @"</body></html>";
    
    
    NSString *formatedHTML = nil;
    if ([self isNoImgMod]) {//无图
        formatedHTML = [_detailModel.content stringByReplacingOccurrencesOfString:@"src=\"" withString:@"src=\"detail_click_show_img.png\" image_src=\""];
    }
    else{//有图
        formatedHTML = [_detailModel.content stringByReplacingOccurrencesOfString:@"src=\"" withString:@"src=\"detail_loading_img.png\" image_src=\""];
    }
    
    NSString *htmlString = [NSString stringWithFormat:@"%@%@%@%@%@%@",
                            htmlStart,
                            titleString,
                            date,
                            formatedHTML,
                            [self articleTagElementsText],
                            htmlEndString];
    
//    NSURL *baseURL = [NSURL fileURLWithPath:[[NSBundle mainBundle] bundlePath]];
     NSURL *baseURL = [NSURL fileURLWithPath:[[NSBundle mainBundle] bundlePath]];
//     NSURL *baseURL = [NSURL fileURLWithPath:[[NSBundle mainBundle] resourcePath]];
    
    [self.articleDetailView loadHTMLString:htmlString baseURL:baseURL];
    
    NSURLCache * cache = [NSURLCache sharedURLCache];
    [cache removeAllCachedResponses];
    [cache setDiskCapacity:0];
    [cache setMemoryCapacity:0];
}

//加载远程url
- (void)loadRemoteUrl{
    
            __weak typeof(self) weakSelf = self;
    [YZHomeService doPostsListPageCounts:@"10"
                             currentPage:@"1"
                             categoryUrl:self.urlString
                                 success:^(NSArray *postsList)
     {
         YZPostsModel *model = postsList.firstObject;
         
        _viewModel = [[YZHomeViewModel alloc] init];
        _viewModel.poastModel = model;
         _detailModel = [[YZArticelDetailModel alloc] init];
         _detailModel.content = _viewModel.poastModel.content;
         
         [weakSelf refreshUI];

     } failure:^(NSString *statusCode, NSString *error) {
         [self dismissHuDForView:self.view];
         
         [self presentSheet:error ForView:self.view];
         
     }];

}


//获取文章缓存
//获取文章详情
- (void)loadDetailData:(BOOL)isRefresh{
    
    NSString *cacheKey = [[NSString stringWithFormat:@"%@-%@-%@",@(_viewModel.poastModel.postID),_viewModel.poastModel.modified,[Config getPictureQualityMod]] md5Hash];
    
    if (isRefresh) {
        __weak typeof(self) weakSelf = self;
        [YZHomeService doArticleDetailWith:_viewModel.poastModel.postID
                                  cacheKey:(NSString *)cacheKey
                                 articleUrl:self.urlString
                                   success:^(YZArticelDetailModel *detailModel)
         {
             weakSelf.detailModel = detailModel;

             [weakSelf refreshUI];
             
         } failure:^(NSString *statusCode, NSString *error) {
             [weakSelf presentSheet:error ForView:weakSelf.view];
         }];
    }
}

//获取评论列表
- (void)loadCommentsData{
    [_moreCommentsBtn setTitle:@"加载中..." forState:UIControlStateNormal];
    
     __weak typeof(self) weakSelf = self;
    [YZHomeService doGetCommentsWith:_viewModel.poastModel.postID
                          pageCounts:@"10"
                         currentPage:[NSString stringWithFormat:@"%@",@(self.currentPage)]
                             success:^(NSArray *commentsList)
     {
         if (weakSelf) {
             
             [commentsList enumerateObjectsUsingBlock:^(YZADetailCommentModel *model, NSUInteger idx, BOOL *stop) {
                 
                 YZDetailCommentViewModel *viewModel = [[YZDetailCommentViewModel alloc] init];
                 viewModel.model = model;
                 [weakSelf.commentsDataArray addObject:viewModel];
                 
             }];
             
             [weakSelf.tableView reloadData];
             
             if (commentsList.count < 10 && commentsList.count > 0) {
                 [weakSelf.moreCommentsBtn setTitle:LocalizedString(@"NO_MORE") forState:UIControlStateNormal];
                weakSelf.moreCommentsBtn.hidden = YES;
             }
             else if (commentsList.count == 0){
                 [weakSelf.moreCommentsBtn setTitle:LocalizedString(@"NO_COMMENT") forState:UIControlStateNormal];
             }
             else{
                 [weakSelf.moreCommentsBtn setTitle:LocalizedString(@"LOOK_MORE_COMMENT") forState:UIControlStateNormal];
             }
             
             
             weakSelf.currentPage ++;
         }
         
     } failure:^(NSString *statusCode, NSString *error) {
         [weakSelf.moreCommentsBtn setTitle:LocalizedString(@"LOOK_MORE_COMMENT") forState:UIControlStateNormal];
     }];
    
}


- (NSString *)parserData:(NSString *)name
{
    NSString *resourcePath = [[NSBundle mainBundle] resourcePath];
    
    NSString *path = [resourcePath stringByAppendingPathComponent:name];
    
    NSData *data = [NSData dataWithContentsOfFile:path];
    
    NSString *ordersString= [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    
    return ordersString;
    
}


- (void)reloadRowsAtIndexPath:(NSIndexPath *)indexPath{
    
    YZDetailCommentViewModel *viewModel = self.commentsDataArray[indexPath.row];
    
    if (viewModel.isOpenShow) {
        
        
        
        if (_lastOpenCommentModel !=  viewModel || !_lastOpenCommentModel.isRealHeight) {
            
            _lastOpenCommentModel.isRealHeight = NO;
            _lastOpenCommentModel = viewModel;
            
            _lastOpenCommentModel.isRealHeight = YES;
            
        }
        else{
            viewModel.isRealHeight = NO;
        }
        
        NSArray *indexArray=[NSArray arrayWithObject:indexPath];
        [self.tableView reloadRowsAtIndexPaths:indexArray withRowAnimation:UITableViewRowAnimationAutomatic];
        
    }
}
#pragma mark-- JSBridge

- (void)setupJSBridge{
    if (!_jsBridge) {
        _jsBridge = [WebViewJavascriptBridge bridgeForWebView:self.articleDetailView webViewDelegate:self handler:^(NSArray *data, WVJBResponseCallback responseCallback) {
            
            //            NSLog(@"ObjC received message from JS: %@\n%@", [data class], data);
            
            //是否是无图模式
            if (![self isNoImgMod]) {
                if (data) {
                    [self downloadAllImages:data];
                }
            }
            
            
            if (responseCallback) {
                responseCallback(@"Response for message from ObjC");
            }
        }];
        
        
        [_jsBridge registerHandler:@"appointImageDownload" handler:^(id data, WVJBResponseCallback responseCallback) {
           
            [self downloadImageWithUrlString:data responseCallback:responseCallback];
            
        }];
    }
}


- (void)downloadImageWithUrlString:(NSString*)imageUrlString responseCallback:(WVJBResponseCallback) responseCallback{

    if (![imageUrlString isKindOfClass:[NSNull class]]) {
        NSString *urlStr= [imageUrlString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSURL *url = [NSURL URLWithString:imageUrlString];
        
        if (!url) {
            url = [NSURL URLWithString:urlStr];
        }
        
        SDWebImageManager *manager = [SDWebImageManager sharedManager];
        [manager downloadImageWithURL:url options:SDWebImageHighPriority progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
           
            if (image) {
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
                    //把图片在磁盘中的地址传回给JS
                    NSString *key = [manager cacheKeyForURL:imageURL];
                    NSString *cachedPath = [manager.imageCache defaultCachePathForKey:key];
                    
                    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                        responseCallback(cachedPath);
                        [self performSelector:@selector(allImageDownloadComplete) withObject:nil afterDelay:0.1];

                    });

                    
                });
            }

        }];
    }
    
}

#pragma mark-- 页面标签

- (NSString *)articleTagElementsText{
    NSString *div = @"";
    
    if (    _viewModel.poastModel.terms.post_tag.count > 0) {
        
        
        
        
      __block  NSString *elements = @"";
        
        [_viewModel.poastModel.terms.post_tag enumerateObjectsUsingBlock:^(YZPostTag *obj, NSUInteger idx, BOOL *stop) {
            elements = [NSString stringWithFormat:@"%@<a href ='%@'>%@</a>",elements,obj.link,obj.name];
        }];
        
        div = [NSString stringWithFormat:@"<div id='TagsDiv'>%@</div>",elements];
        
    }
    
    return div;
}

#pragma mark-- 下载html中图片
- (BOOL)isNoImgMod{
    return [[Config getPictureQualityMod] isEqualToString:@"1"];
}

-(void)downloadAllImages:(NSArray *)imageUrls{
    
    if (imageUrls.count == 0) {
        return;
    }
    _contentImages = [NSMutableArray arrayWithCapacity:imageUrls.count];
    
    SDWebImageManager *manager = [SDWebImageManager sharedManager];
    
    [imageUrls enumerateObjectsUsingBlock:^(NSString  *imageUrlString, NSUInteger idx, BOOL *stop) {
        
        
        if (![imageUrlString isKindOfClass:[NSNull class]]) {
            NSString *urlStr= [imageUrlString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            NSURL *url = [NSURL URLWithString:imageUrlString];
            if (!url) {
                url = [NSURL URLWithString:urlStr];
            }
            [manager downloadImageWithURL:url options:SDWebImageHighPriority progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
                if (image) {
                    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), ^{
                        //把图片在磁盘中的地址传回给JS
                        NSString *key = [manager cacheKeyForURL:imageURL];
                        NSString *cachedPath = [manager.imageCache defaultCachePathForKey:key];
                        
                        
                        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                            [_jsBridge callHandler:@"imageDownloadComplete" data:@[imageUrlString, cachedPath]];
                            
                            if (idx== imageUrls.count-1) {
                                [self performSelector:@selector(allImageDownloadComplete) withObject:nil afterDelay:0.1];
                            }
                        });

                    });
                }
                

            }];
        }
        
        
    }];
    
    

}

- (void)allImageDownloadComplete{
    [self didSelectIndex:-100];
}

#pragma mark -- Actions
- (void)showFontSettingView{
     [self.fontSetSheet showInView:self.navigationController];
}

- (void)showWriteCommentAction:(UIButton *)button{

  
    if (self.viewModel.commentType == NotAllowCommentType){
        [self presentSheet:LocalizedString(@"NO_OPEN_COMMENT") ForView:self.navigationController.view];
    }
    else if ([Config isLogin]) {
        [self.navigationController.view addSubview:self.writeCommentView];
        [self.writeCommentView.contentText becomeFirstResponder];
    }
    else if (self.viewModel.commentType == NeedLoninCommentType) {
          //必须是注册用户，所以必须登录
        YZLoginController *login= [YZLoginController new];
        [[UIApplication sharedApplication].keyWindow.rootViewController presentViewController:login
                                                                                     animated:YES completion:^{
                                                                                     }];
    }
    else{//允许匿名评论或不允许匿名评论
        YZCommentController *commmetController = [[YZCommentController alloc] init];
        commmetController.delegate = self;
        [self.navigationController pushViewController:commmetController animated:YES];
    }

    
}

- (void)locateToCommentAction:(UIButton *)button{
    
    [UIView animateWithDuration:0.33 animations:^{
        self.tableView.contentOffset = CGPointMake(0, self.articleDetailView.height);
    }];
}


-(void)sendCommentAction{
    
    NSString *comment = self.writeCommentView.contentText.text;
    NSString *articleId = [NSString stringWithFormat:@"%@",@(self.viewModel.poastModel.postID)];
    
    if ([comment isEqualToString:@""]) {
        [self presentSheet:LocalizedString(@"INPUT_COMMENT") ForView:self.navigationController.view];
    }
    else{
        [self endEditing:nil];
        [self showLoadingWithTitle:LocalizedString(@"SENDING_COMMENT") AddedTo:self.navigationController.view];
        [YZHomeService doSendCommentsWithComment:comment
                                       articleId:articleId
                                          author:@""
                                           email:@""
                                         success:^(NSDictionary *dic){
            [self dismissHuDForView:self.navigationController.view];
            self.writeCommentView.contentText.text = @"";
            [self presentSheet:LocalizedString(@"PUBLISH_COMMENT_SUCCUSS") ForView:self.navigationController.view];
            
            
            YZDetailCommentViewModel *viewModel = [[YZDetailCommentViewModel alloc] init];
            YZADetailCommentModel *model = [[YZADetailCommentModel alloc] init];
            
             NSDate *  senddate=[NSDate date];
             NSDateFormatter  *dateformatter=[[NSDateFormatter alloc] init];
             [dateformatter setDateFormat:@"YYYY-MM-ddTHH:mm:ss"];
             NSString *  locationString=[dateformatter stringFromDate:senddate];
             model.date_gmt = locationString;
            
            YZADetailAuthor *author = [[YZADetailAuthor alloc] init];
            author.nickname = [Config getNickName]?[Config getNickName] : LocalizedString(@"NICK_NAME");
            
            model.author = author;
            model.content = comment;
            viewModel.model = model;
            
            [self.commentsDataArray insertObject:viewModel atIndex:0];

            NSIndexPath *path = [NSIndexPath indexPathForRow:0 inSection:0];
            [self.tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:path] withRowAnimation:UITableViewRowAnimationAutomatic];
            [self performSelector:@selector(reloadData) withObject:nil afterDelay:0.33];
            
        } failure:^(NSString *statusCode, NSString *error) {
            [self dismissHuDForView:self.navigationController.view];
            [self presentSheet:error ForView:self.navigationController.view];
        }];
    }
    
}

- (void)favBtnAction:(UIButton *)btn{
    if ([Config isLogin]) {
        if (btn.selected) {
            [self removeFavoriteAction];
        }else{
            [self addFavoriteAction];
        }
    }else{
        if (btn.selected) {
            [self.saveModel deleteApostWithId:self.viewModel.poastModel.postID];
        }else{
            [self.saveModel saveApostModel:self.viewModel.poastModel];
        }
        btn.selected = !btn.selected;
        
//        YZLoginController *login= [YZLoginController new];
//        [[UIApplication sharedApplication].keyWindow.rootViewController presentViewController:login
//                                                                                     animated:YES completion:^{
//                                                                                     }];
    }
    
    
   
}

- (void)showShareViewAction:(UIButton *)btn{
    
    YZShareActionView *sheet = [[YZShareActionView alloc] init];
    
    __weak typeof(sheet) weakSheet = sheet;
    
    
    if ([AppConfigManager sharedInstance].wechatAppId.length > 0) {
        [sheet addIconWithTitle:LocalizedString(@"share_wechat") imageName:@"share_wechat" block:^{
            [weakSheet dismissView];
            [self shareWithPlatformType:SSDKPlatformSubTypeWechatSession];
        } atIndex:-1];
        
        
        [sheet addIconWithTitle:LocalizedString(@"share_wechat_friend") imageName:@"share_wechat_friend" block:^{
            [weakSheet dismissView];
            [self shareWithPlatformType:SSDKPlatformSubTypeWechatTimeline];
        } atIndex:-1];
    }
    

    if ([AppConfigManager sharedInstance].weiboAppKey.length > 0) {
        [sheet addIconWithTitle:LocalizedString(@"share_weibo") imageName:@"share_webo" block:^{
            [weakSheet dismissView];
            [self shareWithPlatformType:SSDKPlatformTypeSinaWeibo];
        } atIndex:-1];
        
    }
    
    
     if ([AppConfigManager sharedInstance].QQAppId.length > 0) {
    
             [sheet addIconWithTitle:LocalizedString(@"share_qq") imageName:@"share_QQ" block:^{
                 [weakSheet dismissView];
                 [self shareWithPlatformType:SSDKPlatformSubTypeQQFriend];
             } atIndex:-1];
             
             
             [sheet addIconWithTitle:LocalizedString(@"share_qzone") imageName:@"share_qzone" block:^{
                 [weakSheet dismissView];
                 [self shareWithPlatformType:SSDKPlatformSubTypeQZone];
             } atIndex:-1];
         }

    

    
    
    [sheet addIconWithTitle:LocalizedString(@"share_copy_link") imageName:@"share_copy_link" block:^{
        
        NSString *linkUrl = [NSString stringWithFormat:@"%@/?p=%@",[AppConfigManager sharedInstance].appHttpServer,@(_viewModel.poastModel.postID)];

        UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
        pasteboard.string = linkUrl;
        [weakSheet dismissView];
        
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"复制成功"
                                                            message:nil
                                                           delegate:nil
                                                  cancelButtonTitle:@"确定"
                                                  otherButtonTitles:nil];
        [alertView show];
        
    } atIndex:-1];
    
    
    
    [sheet showOnView:self.view.window];
    
    
}

- (void)addFavoriteAction{
    
    NSString *postId = [NSString stringWithFormat:@"%@",@(self.viewModel.poastModel.postID)];
    [self showLoadingAddedTo:self.navigationController.view];
    [YZFavService doAddFavoriteWithPostId:postId success:^(id responseDic) {
        [self dismissHuDForView:self.navigationController.view];
        
        [self presentSheet:LocalizedString(@"SAVE_SUCCESS") ForView:self.navigationController.view];
        _bottomBar.favBtn.selected = YES;
        NSString *cacheKey = [[NSString stringWithFormat:@"%@-%@",@(_viewModel.poastModel.postID),_viewModel.poastModel.modified] md5Hash];
        _detailModel.is_favorited = YES;
        [[PINCache sharedCache] setObject:[_detailModel keyValues] forKey:cacheKey block:nil];
        
    } failure:^(NSString *statusCode, NSString *error) {
        [self dismissHuDForView:self.navigationController.view];
        [self presentSheet:error ForView:self.navigationController.view];
    }];
}

- (void)removeFavoriteAction{
    NSString *postId = [NSString stringWithFormat:@"%@",@(self.viewModel.poastModel.postID)];
    [self showLoadingAddedTo:self.navigationController.view];
    [YZFavService doRemoveFavoriteWithPostId:postId success:^(NSDictionary *dic) {
       [self dismissHuDForView:self.navigationController.view];
       [self presentSheet:LocalizedString(@"CANCEL_SAVE") ForView:self.navigationController.view];
         _bottomBar.favBtn.selected = NO;
        NSString *cacheKey = [[NSString stringWithFormat:@"%@-%@",@(_viewModel.poastModel.postID),_viewModel.poastModel.modified] md5Hash];
        _detailModel.is_favorited = NO;
        [[PINCache sharedCache] setObject:[_detailModel keyValues] forKey:cacheKey block:nil];
        
    } failure:^(NSString *statusCode, NSString *error) {
        [self dismissHuDForView:self.navigationController.view];
        [self presentSheet:error ForView:self.navigationController.view];
        
    }];
}


- (void)reloadData{
    [self.tableView reloadData];
}
-(void)endEditing:(UIButton *)btn{
    [self.writeCommentView endEditing:YES];
}

- (void)insertNewCommentContent{
    
}


#pragma mark - 键盘处理
#pragma mark 键盘即将显示
- (void)keyBoardWillShow:(NSNotification *)note{
    
    CGRect rect = [note.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
    CGFloat ty = - rect.size.height;
    
    [UIView animateWithDuration:[note.userInfo[UIKeyboardAnimationDurationUserInfoKey] doubleValue] animations:^{
        self.writeCommentView.transform = CGAffineTransformMakeTranslation(0, ty);
    }];
    
}
#pragma mark 键盘即将退出
- (void)keyBoardWillHide:(NSNotification *)note{
    CGRect rect = [note.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
    CGFloat ty = - rect.size.height;
    [UIView animateWithDuration:[note.userInfo[UIKeyboardAnimationDurationUserInfoKey] doubleValue] animations:^{
         self.writeCommentView.transform = CGAffineTransformMakeTranslation(0, -ty);;
    } completion:^(BOOL finished) {
//        [self.writeCommentView removeFromSuperview];
    }];
}


-(void)didSelectIndex:(NSInteger)index{
    
    if (index>-1) {
        [Config saveFontSize:index];
    }
    
    NSString *textFontSizes = [Config getFontSize];
    
    NSString *jsString = [[NSString alloc] initWithFormat:@"document.getElementsByTagName('body')[0].style.webkitTextSizeAdjust= '%@%%'",
                          @([textFontSizes integerValue])];
    [self.articleDetailView stringByEvaluatingJavaScriptFromString:jsString];
    
    [(YZWebView *)self.articleDetailView configureViews];

    
    CGRect frame = _articleDetailView.frame;
    frame.size.height = 1;
    _articleDetailView.frame = frame;
    CGSize fittingSize = [_articleDetailView sizeThatFits:CGSizeZero];
    frame.size = fittingSize;
    _articleDetailView.frame = frame;
    
    self.tableView.tableHeaderView = self.articleDetailView;
    
}

#pragma mark-- share 

- (void)shareWithPlatformType:(SSDKPlatformType)platformType{
    
    NSString *linkUrl = [NSString stringWithFormat:@"%@/?p=%@",[AppConfigManager sharedInstance].appHttpServer,@(_viewModel.poastModel.postID)];
    
     NSString *shareTilte = [NSString stringWithFormat:@"%@%@",_detailModel.title,linkUrl];
     NSString *shareContent = @"       ";
    
     //1、创建分享参数
     NSMutableDictionary *shareParams = [NSMutableDictionary dictionary];
    
    NSArray* imageArray = nil;
    if (_viewModel.featuredImages.count > 0) {
        UIImageView *imageView = _viewModel.featuredImages.firstObject;
        
        UIImage *image = [UIImage scaleImage:imageView.image scaleFactor:0.6];
        imageArray = @[image];
    }
    else{
        imageArray = @[[UIImage imageNamed:@"AppIcon60x60"]];
    }
    
     if (imageArray)
     {
         [shareParams SSDKSetupShareParamsByText:shareTilte
                                          images:imageArray
                                             url:[NSURL URLWithString:linkUrl]
                                           title:shareContent
                                            type:SSDKContentTypeAuto];
     }
     
     //2、分享
    [ShareSDK share:platformType
         parameters:shareParams
     onStateChanged:^(SSDKResponseState state, NSDictionary *userData, SSDKContentEntity *contentEntity, NSError *error) {
         
         switch (state) {
             case SSDKResponseStateSuccess:
             {
                 UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"分享成功"
                                                                     message:nil
                                                                    delegate:nil
                                                           cancelButtonTitle:@"确定"
                                                           otherButtonTitles:nil];
                 [alertView show];
                 break;
             }
             case SSDKResponseStateFail:
             {
                 UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"分享失败"
                                                                     message:[NSString stringWithFormat:@"%@", error]
                                                                    delegate:nil
                                                           cancelButtonTitle:@"确定"
                                                           otherButtonTitles:nil];
                 [alertView show];
                 break;
             }
             case SSDKResponseStateCancel:
             {
//                 UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"分享已取消"
//                                                                     message:nil
//                                                                    delegate:nil
//                                                           cancelButtonTitle:@"确定"
//                                                           otherButtonTitles:nil];
//                 [alertView show];
                 break;
             }
             default:
                 break;
         }
     }];
    
}

@end
