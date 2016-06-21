//
//  YZLeftViewController.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/27.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//


#import "YZLeftViewController.h"
#import "YZLoginController.h"
#import "BuildConfig.h"
#import "UIViewExt.h"
#import "YZLeftViewCell.h"
#import "YZBaseViewController.h"
#import "Config.h"
#import "YZSettingController.h"
#import "YZFavoriteController.h"
#import "YZCommentRecordsController.h"
#import "YZHistoryController.h"
#import "UIImage+SNAdditions.h"
#import "YZPersonInfoController.h"

#import "UIImageView+AFNetworking.h"
#import "YZSavePostDBModel.h"

#define  kLeftTableViewWidth  AutoDeviceWidth(225)
#define  kIconWidth (AutoDeviceWidth(260.0f)- 2 * AutoDeviceWidth(30))

static NSString * const kLeftViewControllerCellReuseId = @"kICSColorsViewControllerCellReuseId";

@interface YZLeftViewController ()<UITableViewDataSource, UITableViewDelegate>

@property(nonatomic, strong) NSArray *colors;
@property(nonatomic, assign) NSInteger previousRow;
@property(nonatomic, strong) UITableView *tableView;

@property (nonatomic, strong)UIView *topView;
@property (nonatomic, strong)UIView *bottomView;


@property (nonatomic, strong)UIButton *loginButton;
@property (nonatomic, strong)UIButton *logoutButton;
@property (nonatomic, strong)UIImageView *userIcon;


@property (nonatomic, strong)NSArray  *menuTitles;
@property (nonatomic, strong)NSArray  *subClassTitles;
@property (nonatomic, strong)NSArray  *menuIcons;
@property (nonatomic, strong)NSArray  *menuHighlightedIcons;
@property (nonatomic, strong)NSArray  *menuControllers;
@property (strong,nonatomic)YZSavePostDBModel *saveModel;
@end


@implementation YZLeftViewController


#pragma mark - Managing the view

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.view.backgroundColor = RGBCOLOR(37, 37, 37);
    
    self.subClassTitles = @[LocalizedString(@"MY_SAVE_TITLE"),LocalizedString(@"MY_COMMENT_TITLE"),LocalizedString(@"MY_HISTORY_TITLE"),LocalizedString(@"MY_SETTING_TITLE")];

    self.menuTitles = @[LocalizedString(@"MY_SAVE"),LocalizedString(@"MY_COMMENT"),LocalizedString(@"MY_HISTORY"),LocalizedString(@"MY_SETTING")];
    self.menuIcons = @[@"left_fav_normal",@"left_comment_normal",@"left_history_normal",@"left_setting_normal"];
    self.menuHighlightedIcons = @[@"left_fav_highlighted",@"left_comment_highlighted",@"left_history_highlighted",@"left_setting_highlighted"];
    
    self.menuControllers = @[[[YZFavoriteController alloc] init],[[YZCommentRecordsController alloc] init],[[YZHistoryController alloc]init],[[YZSettingController alloc] init]];
    [self makTopAndBottomViews];

}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    if ([Config isLogin]) {
        [_loginButton setTitle:[Config getNickName] forState:UIControlStateNormal];
        YZLoginModel *userModel = [Config loadLoginUser];
        [self.userIcon setImageWithURL:[NSURL URLWithString:userModel.avatar] placeholderImage:[UIImage imageNamed:@"default_user_icon"]];
        _loginButton.enabled = NO;
        _logoutButton.hidden = NO;
//        _userIcon.userInteractionEnabled = NO;
    }
    else{
        _logoutButton.hidden = YES;
         _loginButton.enabled = YES;
        
        [_loginButton setTitle:LocalizedString(@"HELLO_LOGIN") forState:UIControlStateNormal];
    }
    
}

- (BOOL)prefersStatusBarHidden
{
    return YES;
}


#pragma mark -  SubViews

- (UIImageView *)userIcon
{
    if (!_userIcon) {
        
        _userIcon = [[UIImageView alloc] initWithFrame:
                     CGRectMake((kIconWidth-80)/2, AutoDeviceHeight(40), 80, 80)];
        _userIcon.backgroundColor = [UIColor whiteColor];
        _userIcon.image = [UIImage imageNamed:(@"default_user_icon")];
        _userIcon.layer.cornerRadius = 40;
        _userIcon.layer.masksToBounds = YES;
        _userIcon.contentMode = UIViewContentModeScaleAspectFill;
        _userIcon.userInteractionEnabled = YES;
        
        UITapGestureRecognizer*tapGesture = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(loginAction)];
        
        [_userIcon addGestureRecognizer:tapGesture];
    }
    return _userIcon;
}

- (void)makTopAndBottomViews{
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStylePlain];
    self.tableView.delegate = self;
    self.tableView.dataSource = self;
    
    self.tableView.frame = CGRectMake(AutoDeviceWidth(30), 0, kLeftTableViewWidth, kSCREEN_HEIGHT);
    [self.tableView registerClass:[YZLeftViewCell class] forCellReuseIdentifier:kLeftViewControllerCellReuseId];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.backgroundColor = [UIColor clearColor];
    self.tableView.rowHeight = AutoDeviceHeight(55);
    [self.view addSubview:self.tableView];
    
    
    self.topView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kLeftTableViewWidth, AutoDeviceHeight(225))];
    
    self.bottomView = [[UIView alloc] initWithFrame:CGRectMake(0,
                                                               0,
                                                               AutoDeviceWidth(205),
                                                               kSCREEN_HEIGHT-self.menuTitles.count*AutoDeviceHeight(55)-self.topView.height)];
    
    self.tableView.tableHeaderView = self.topView;
    self.tableView.tableFooterView = self.bottomView;
    
    [self.topView addSubview:self.loginButton];
    [self.topView addSubview:self.userIcon];
    
    [self.bottomView addSubview:self.logoutButton];
}

-(UIButton *)loginButton{
    if (!_loginButton) {
        _loginButton = [UIButton buttonWithType:UIButtonTypeCustom];
        UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:13.0];
        _loginButton.titleLabel.font = font;
        [_loginButton setTitle:LocalizedString(@"HELLO_LOGIN") forState:UIControlStateNormal];
        
        [_loginButton setTitleColor:RGBCOLOR(201, 201, 201) forState:UIControlStateNormal];
        [_loginButton setTitleColor:RGBCOLOR(255, 255, 255) forState:UIControlStateHighlighted];
        
        _loginButton.frame = CGRectMake(0, self.userIcon.bottom + 12, kIconWidth, 14);
        
        [_loginButton addTarget:self action:@selector(loginAction) forControlEvents:UIControlEventTouchUpInside];
        
    }
    return _loginButton;
}

- (YZSavePostDBModel *)saveModel
{
    if (!_saveModel) {
        _saveModel = [[YZSavePostDBModel alloc]init];
    }
    return _saveModel;
}

-(void)loginAction{
    if ([Config isLogin])
    {
        YZPersonInfoController *personInfo = [[YZPersonInfoController alloc]init];
//        [(UINavigationController *)self.drawer.centerViewController pushViewController:personInfo animated:YES];
        
        typeof(self) __weak weakSelf = self;
        UINavigationController *nav = (UINavigationController *)weakSelf.drawer.centerViewController;
        
        [self.drawer reloadCenterViewControllerUsingBlock:^(){
            NSParameterAssert(weakSelf.menuTitles);
            
            [nav pushViewController:personInfo animated:NO];
            
        }];
        
    }
    else
    {
        YZLoginController *login= [[YZLoginController alloc] init];
        [self presentViewController:login  animated:YES completion:nil];
    }
}

- (void)logoutAction{
    
    _userIcon.image = [UIImage imageNamed:(@"default_user_icon")];
    [Config clearCookie];
    [self.saveModel deleteAllPostModel];
    [self.drawer close];
}

-(UIButton *)logoutButton{
    if (!_logoutButton) {
        
        _logoutButton = [UIButton buttonWithType:UIButtonTypeCustom];
        UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:14.0];
        _logoutButton.titleLabel.font = font;
//        [_logoutButton setTitle:LocalizedString(@"LOGOUT_BTN") forState:UIControlStateNormal];
        
        UIImage * image = [UIImage streImageNamed:@"left_logout_bg" capX:50 capY:0];
        [_logoutButton setBackgroundImage:image forState:UIControlStateNormal];
        [_logoutButton setBackgroundImage:image forState:UIControlStateHighlighted];
        
        [_logoutButton setTitleColor:RGBCOLOR(102, 102, 102) forState:UIControlStateNormal];
        [_logoutButton setTitleColor:RGBCOLOR(255, 255, 255) forState:UIControlStateHighlighted];
        _logoutButton.frame = CGRectMake(0, self.bottomView.height-45, AutoDeviceWidth(205), 30);
        [_logoutButton addTarget:self action:@selector(logoutAction) forControlEvents:UIControlEventTouchUpInside];
        
    }
    return _logoutButton;
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    NSParameterAssert(self.menuTitles);
    return self.menuTitles.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSParameterAssert(self.menuTitles);
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kLeftViewControllerCellReuseId
                                                            forIndexPath:indexPath];
    cell.textLabel.text = self.menuTitles[indexPath.row];
    cell.textLabel.textColor = RGBCOLOR(201, 201, 201);
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:14];
    [cell.textLabel setFont:font];

    cell.backgroundColor = [UIColor clearColor];
    
    cell.imageView.image = [UIImage imageNamed:self.menuIcons[indexPath.row]];

    cell.imageView.highlightedImage = [UIImage imageNamed:self.menuHighlightedIcons[indexPath.row]];
    
    
    if (indexPath.row < self.menuTitles.count-1) {
        CALayer *bottomBorder = [CALayer layer];
        
        bottomBorder.frame = CGRectMake(45, cell.frame.size.height-1, cell.frame.size.width, 1.0f);
        
        bottomBorder.backgroundColor = RGBCOLOR(50, 50, 50).CGColor;
        
        [cell.contentView.layer addSublayer:bottomBorder];
    }
    
    
    NSMutableAttributedString *str = [[NSMutableAttributedString alloc] initWithString:self.menuTitles[indexPath.row]];

    [str addAttribute:NSFontAttributeName value:[UIFont fontWithName:@"CourierNewPSMT" size:14.0] range:NSMakeRange(cell.textLabel.text.length-7, 7)];
    cell.textLabel.attributedText = str;
    
    return cell;
}

#pragma mark - Table view delegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row ==1 && ![Config isLogin]) {
        [self loginAction];
        return;
    }
    
    YZBaseViewController *nextC = self.menuControllers[indexPath.row];

    nextC.tableView.tableFooterView = [UIView new];
    nextC.isBackBarButtonItemShow = YES;
    nextC.title = self.subClassTitles[indexPath.row];

    typeof(self) __weak weakSelf = self;
    UINavigationController *nav = (UINavigationController *)weakSelf.drawer.centerViewController;

    [self.drawer reloadCenterViewControllerUsingBlock:^(){
        NSParameterAssert(weakSelf.menuTitles);

        [nav pushViewController:nextC animated:NO];
        
    }];

    self.previousRow = indexPath.row;
}

#pragma mark - ICSDrawerControllerPresenting

- (void)drawerControllerWillOpen:(ICSDrawerController *)drawerController
{
    self.view.userInteractionEnabled = NO;
}

- (void)drawerControllerDidOpen:(ICSDrawerController *)drawerController
{
    self.view.userInteractionEnabled = YES;
}

- (void)drawerControllerWillClose:(ICSDrawerController *)drawerController
{
    self.view.userInteractionEnabled = NO;
}

- (void)drawerControllerDidClose:(ICSDrawerController *)drawerController
{
    self.view.userInteractionEnabled = YES;
}

@end
