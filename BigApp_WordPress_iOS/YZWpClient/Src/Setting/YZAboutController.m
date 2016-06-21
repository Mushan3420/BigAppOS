//
//  YZAboutController.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/22.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZAboutController.h"
#import "UIImage+SNAdditions.h"
#import "UIViewExt.h"
#import "BuildConfig.h"
#import "AppDelegate.h"

@interface YZAboutController ()
{
    AppDelegate             *appDelegate;
}


@property (nonatomic, strong)UILabel *SvnVersionView;
@property (nonatomic,strong)UIImageView *doteView;

@end

@implementation YZAboutController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.isBackBarButtonItemShow = YES;
    self.title = NSLocalizedString(@"about_us", nil);
    
    self.edgesForExtendedLayout = UIRectEdgeAll;
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStylePlain];
    self.tableView.backgroundColor = [UIColor colorWithRed:242.0/255 green:242.0/255 blue:242.0/255 alpha:1];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.showsVerticalScrollIndicator = NO;
    
    self.tableView.tableHeaderView = [self headView];
    self.tableView.tableFooterView = [self footView];
    
    appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
}

- (UIView *)headView
{
    UIView *headView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kSCREEN_WIDTH, 160)];
    headView.backgroundColor = [UIColor clearColor];
    
    UIImageView *iconImage = [[UIImageView alloc]initWithFrame:CGRectMake((kSCREEN_WIDTH-70)/2, 45, 70, 70)];
    [iconImage setImage:[UIImage imageNamed:@"AppIcon60x60"]];
    [headView addSubview:iconImage];
    
    UILabel *versionLab = [[UILabel alloc]initWithFrame:CGRectMake(0, 114, kSCREEN_WIDTH, 30)];
    versionLab.backgroundColor = [UIColor clearColor];
    versionLab.textColor = RGBCOLOR(118, 119, 120);
    versionLab.font = [UIFont systemFontOfSize:14];
    versionLab.textAlignment = NSTextAlignmentCenter;
    NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
    NSString *build = [NSString stringWithFormat:@"version %@ build %@",[infoDictionary objectForKey:@"CFBundleShortVersionString"],[infoDictionary objectForKey:@"CFBundleVersion"]];//

    versionLab.text = build;
    [headView addSubview:versionLab];
    return headView;
}

- (UIView *)footView
{
    UIView *footView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kSCREEN_WIDTH, 160)];
    footView.backgroundColor = [UIColor clearColor];
    
    UILabel *downLab = [[UILabel alloc]initWithFrame:CGRectMake(0, 20, kSCREEN_WIDTH, 60)];
    downLab.backgroundColor = [UIColor clearColor];
    downLab.textColor = RGBCOLOR(118, 119, 120);
    downLab.font = [UIFont systemFontOfSize:14];
    downLab.textAlignment = NSTextAlignmentCenter;
    downLab.numberOfLines = 0;
    NSString *build = NSLocalizedString(@"about_big", nil);;
    
    downLab.text = build;
    [footView addSubview:downLab];
    downLab.hidden = YES;
    return footView;
}

- (UILabel *)SvnVersionView
{
    _SvnVersionView = [[UILabel alloc]initWithFrame:CGRectMake(0, kSCREEN_HEIGHT-30-64, kSCREEN_WIDTH, 30)];
    _SvnVersionView.backgroundColor = [UIColor clearColor];
    _SvnVersionView.font = [UIFont systemFontOfSize:12];
    _SvnVersionView.textAlignment = NSTextAlignmentCenter;
    _SvnVersionView.textColor = RGBCOLOR(118, 119, 120);
    _SvnVersionView.text = SVNVerSionNo;
    return _SvnVersionView;
}



#pragma mark-- UITableViewDelegate,UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    return 40;
}




- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *kCellIdentifier = @"cell_id";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kCellIdentifier];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:kCellIdentifier];
        cell.backgroundColor = [UIColor whiteColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        UIImageView *_line = [[UIImageView alloc] initWithImage:[UIImage streImageNamed:@"separatory_line"]];
        _line.width = kSCREEN_WIDTH;
        [cell.contentView addSubview:_line];
    }
    if (indexPath.row == 0) {
        
        cell.detailTextLabel.font = [UIFont fontWithName:kChineseFontNameXi size:15];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        [cell.contentView addSubview:self.doteView];
    }
    cell.textLabel.backgroundColor = [UIColor clearColor];
    cell.textLabel.font = [UIFont systemFontOfSize:15];
    cell.textLabel.textColor = RGBCOLOR(60, 60, 60);
    
    cell.textLabel.text = NSLocalizedString(@"version_update", nil);
    
    return cell;
}

- (UIImageView *)doteView
{
    _doteView = [[UIImageView alloc]initWithFrame:CGRectMake(kSCREEN_WIDTH-34, 10, 6, 6)];
    _doteView.backgroundColor = [UIColor clearColor];
    [_doteView setImage:[UIImage imageNamed:@"red_dote"]];
    return _doteView;
}

@end
