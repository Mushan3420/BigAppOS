//
//  SettingController.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/7.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZSettingController.h"
#import "UIViewExt.h"
#import "UIImage+SNAdditions.h"
#import "SVProgressHUD.h"
#import "UIViewController+YZProgressHUD.h"
#import "Config.h"
#import "FontSetSheet.h"
#import "YZAboutController.h"
#import "YZOffLineDownloadController.h"
#import "YZOffLineDownloadTool.h"

@interface YZSettingController ()<FontSetSheetDelegate,UIGestureRecognizerDelegate,UIActionSheetDelegate>
{
    NSArray *titles;
    NSArray *otherTiltes;
}
@property (nonatomic, strong)UILabel *buildVersionView;
@property (nonatomic, strong)FontSetSheet *fontSetSheet;
@property (nonatomic, strong)UISwitch *nightModeSwith;



@end

@implementation YZSettingController

- (void)viewDidLoad {
    [super viewDidLoad];

    [self.tableView addSubview:self.buildVersionView];
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStyleGrouped];
    self.tableView.backgroundColor = [UIColor colorWithRed:242.0/255 green:242.0/255 blue:242.0/255 alpha:1];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.showsVerticalScrollIndicator = NO;
    
//    titles = @[LocalizedString(@"CLEAR_CACHE"),LocalizedString(@"FEED_BACK"),LocalizedString(@"OUT_PRASE"),LocalizedString(@"ABOUT_OURS")];
//    otherTiltes = @[LocalizedString(@"FONT_SETTING"),LocalizedString(@"NO_PICTURE"),LocalizedString(@"MODE_NINGT"),LocalizedString(@"FONT_DOWNLOAD")];
    
    titles = @[LocalizedString(@"CLEAR_CACHE"),LocalizedString(@"OFFLINE_CACHE"),LocalizedString(@"ABOUT_OURS")];
    otherTiltes = @[LocalizedString(@"FONT_SETTING"),LocalizedString(@"MODE_NINGT"),LocalizedString(@"NO_PICTURE")];

}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self.tableView reloadData];
//    [[UINavigationBar appearance] setBarTintColor:[UIColor whiteColor]];
//    NSDictionary *attributes=[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor],NSForegroundColorAttributeName,nil];
//    [[UINavigationBar appearance] setTitleTextAttributes:attributes];
}

- (UISwitch *)nightModeSwith
{
    if (_nightModeSwith == nil) {
        _nightModeSwith = [[UISwitch alloc]initWithFrame:CGRectMake(kSCREEN_WIDTH-70, 4, 60, 36)];
        [_nightModeSwith addTarget:self action:@selector(swithNightMode:) forControlEvents:UIControlEventValueChanged];
  
        _nightModeSwith.on = [[Config getThemeName] isEqualToString:[self themeArray][1]];
    
    }
    return _nightModeSwith;
}


-(UILabel *)buildVersionView{
    if (!_buildVersionView) {
        _buildVersionView = [[UILabel alloc] init];
        
        _buildVersionView.textAlignment = NSTextAlignmentCenter;
        _buildVersionView.frame = CGRectMake(0, kSCREEN_HEIGHT - 40 - 64, kSCREEN_WIDTH, 40);
        
        NSDictionary *infoDictionary = [[NSBundle mainBundle] infoDictionary];
        NSString *build = [infoDictionary objectForKey:@"CFBundleVersion"];
        _buildVersionView.text = [NSString stringWithFormat:@"Build %@",build];
        _buildVersionView.textColor = [UIColor lightGrayColor];
    }
    return _buildVersionView;
}

-(FontSetSheet *)fontSetSheet{
    if (!_fontSetSheet) {
        FontSetSheetModel *Model_6 = [[FontSetSheetModel alloc]init];
        NSArray *menuList = [[NSArray alloc]init];
        
        menuList = @[Model_6];
        _fontSetSheet = [[FontSetSheet alloc]initWithlist:menuList height:0];
        _fontSetSheet.delegate = self;
    }

    return _fontSetSheet;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark-- Propertys



#pragma mark-- UITableViewDelegate,UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    if (section == 0) {
      return  titles.count;
    }
    return otherTiltes.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    if (section == 0 ) {
        return 20;
    }
    return 30.f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 10.0001f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    return 40;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{

    if (section == 0) {
        return nil;
    }
    UILabel *sectionView = [[UILabel alloc] initWithFrame:CGRectZero];
    [sectionView setFont:[UIFont fontWithName:kChineseFontNameXi size:12]];
    [sectionView setTextColor:RGBCOLOR(70, 70, 70)];
    [sectionView setTextAlignment:NSTextAlignmentLeft];
    sectionView.text = [NSString stringWithFormat:@"  %@",LocalizedString(@"OTHERS")];
    sectionView.backgroundColor = [UIColor clearColor];
    return sectionView;
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
    if (indexPath.section ==0) {
            cell.textLabel.text = titles[indexPath.row];
        
        if (indexPath.row == 0) {
            cell.detailTextLabel.text = @"109KB";
            NSString *cachPath = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory,NSUserDomainMask, YES) objectAtIndex:0];
            
            float foldeSize =  [self folderSizeAtPath:cachPath];
            if (foldeSize > 1) {
                cell.detailTextLabel.text = [NSString stringWithFormat:@"%.2fM",foldeSize];

            }
            else{
                cell.detailTextLabel.text = [NSString stringWithFormat:@"%.0fKB",foldeSize*1024];

            }
            cell.detailTextLabel.font = [UIFont fontWithName:kChineseFontNameXi size:15];
            cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        }
        
    }
    else if (indexPath.section == 1)
    {
        cell.textLabel.text = otherTiltes[indexPath.row];
        
        switch (indexPath.row) {
            case 1:
            {
                [cell addSubview:self.nightModeSwith];

            }
                break;
                
            case 2:
            {
                cell.detailTextLabel.backgroundColor = [UIColor clearColor];
                cell.detailTextLabel.font = [UIFont systemFontOfSize:14];
                cell.detailTextLabel.textColor = RGBCOLOR(160, 160, 160);
                cell.detailTextLabel.text = [self getImgMod];
            }
                break;
                
            default:
                break;
        }
    }
    else{
        
        cell.textLabel.text = otherTiltes[indexPath.row];
    }
    cell.textLabel.backgroundColor = [UIColor clearColor];
    cell.textLabel.font = [UIFont systemFontOfSize:15];
    cell.textLabel.textColor = RGBCOLOR(60, 60, 60);
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    if (indexPath.section == 0) {
        switch (indexPath.row) {
            case 0:
            {
                [SVProgressHUD setBackgroundColor:[UIColor blackColor]];
                [SVProgressHUD setForegroundColor:[UIColor whiteColor]];
                [SVProgressHUD showWithStatus:@"正在清除缓存..."];
                
                dispatch_async(
                               dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)
                               , ^{
                                   NSString *cachPath = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory,NSUserDomainMask, YES) objectAtIndex:0];
                                   
                                   NSArray *files = [[NSFileManager defaultManager] subpathsAtPath:cachPath];
                                   NSLog(@"files :%@",@([files count]));
                                   for (NSString *p in files) {
                                       NSError *error;
                                       NSString *path = [cachPath stringByAppendingPathComponent:p];
                                       if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {
                                           [[NSFileManager defaultManager] removeItemAtPath:path error:&error];
                                       }
                                   }
                                   [self performSelectorOnMainThread:@selector(clearCacheSuccess) withObject:nil waitUntilDone:YES];});
            }
                break;
            case 1:
            {
                
                [self startOffLineDownload];
//                break;
//                YZOffLineDownloadController *offLine = [[YZOffLineDownloadController alloc] init];
//                [self.navigationController pushViewController:offLine animated:YES];
                break;
            }
            case 2:
            {
                YZAboutController *aboutV = [[YZAboutController alloc]init];
                [self.navigationController pushViewController:aboutV animated:YES];
                break;
            }
            default:
                break;
        }
    }
    
    if (indexPath.section == 1) {
        switch (indexPath.row) {
            case 0:
            {
                [self.fontSetSheet showInView:self.navigationController];
            }
                break;
            case 2:
            {
                [self selectPictureQualitySheet];
            }
                break;
            
                
            default:
                break;
        }
    }
    
}

#pragma mark-- Methods

- (void)selectPictureQualitySheet{

        UIActionSheet *actionSheet = [[UIActionSheet alloc]
                                      initWithTitle:@"非Wifi网络流量"
                                      delegate:self
                                      cancelButtonTitle:@"取消"
                                      destructiveButtonTitle:@"最佳效果 (下载原图)"
                                      otherButtonTitles:@"较省流量 (低质量图)", @"无图模式 (不下图片)",nil];
    actionSheet.destructiveButtonIndex = -1;
        [actionSheet showInView:self.view];

}//非wifi情况下，选择图片质量


- (NSString *)getImgMod{
    NSString *imgMod = [Config getPictureQualityMod];
    
    NSDictionary *modDic = @{@"3":@"最佳效果 (下载原图)",
                             @"2":@"较省流量 (低质量图)",
                             @"1":@"无图模式 (不下图片)"};
    
    return modDic[imgMod];
    
}

#pragma mark EnventClick

- (NSArray *)themeArray
{
    return @[@"Normal",@"Night"];
}

- (void)swithNightMode:(UISwitch *)sender
{

    if (sender.on) {
        [[ThemeManager sharedInstance] setTheme:[[self themeArray] objectAtIndex:1]];
        [Config saveThemeModel:[[self themeArray] objectAtIndex:1]];
    }
    else
    {
        [[ThemeManager sharedInstance] setTheme:[[self themeArray] objectAtIndex:0]];
        [Config saveThemeModel:[[self themeArray] objectAtIndex:0]];
    }
//    [[UINavigationBar appearance] setBarTintColor:[[ThemeManager sharedInstance] colorWithColorName:kTmNavBarColor]];
    [[UINavigationBar appearance] setBarTintColor:[[ThemeManager sharedInstance] colorWithColorName:kTmNavBarColor]];
    [self.navigationController.navigationBar setBarTintColor:[[ThemeManager sharedInstance] colorWithColorName:kTmNavBarColor]];
    [[UIToolbar appearance]setBarTintColor:[[ThemeManager sharedInstance] colorWithColorName:kTmToolBarColor]];

}

- (void)clearCacheSuccess{
        [SVProgressHUD dismiss];

        [self presentSheet:LocalizedString(@"CLEAR_CACHE_SUCCESS") ForView:self.view];
    [self.tableView reloadData];
}

-(void)didSelectIndex:(NSInteger)index{
    [Config saveFontSize:index];
    
}

//单个文件的大小
- (long long) fileSizeAtPath:(NSString*) filePath{
    NSFileManager* manager = [NSFileManager defaultManager];
    if ([manager fileExistsAtPath:filePath]){
        return [[manager attributesOfItemAtPath:filePath error:nil] fileSize];
    }
    return 0;
}
//遍历文件夹获得文件夹大小，返回多少M
- (float ) folderSizeAtPath:(NSString*) folderPath{
    NSFileManager* manager = [NSFileManager defaultManager];
    if (![manager fileExistsAtPath:folderPath]) return 0;
    NSEnumerator *childFilesEnumerator = [[manager subpathsAtPath:folderPath] objectEnumerator];
    NSString* fileName;
    long long folderSize = 0;
    while ((fileName = [childFilesEnumerator nextObject]) != nil){
        NSString* fileAbsolutePath = [folderPath stringByAppendingPathComponent:fileName];
        folderSize += [self fileSizeAtPath:fileAbsolutePath];
    }
    return folderSize/(1024.0*1024.0);
}

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
    
    if (_fontSetSheet.isShow){
        return NO;
    }
    else{
        return YES;
    }
}

#pragma mark-- ActionDelegate
-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex >2) {
        return;
    }
    [Config savePictureQualityMod:buttonIndex];
    [self.tableView reloadData];
    
}


#pragma mark-- 离线浏览


- (void)startOffLineDownload{
    [YZOffLineDownloadTool startOffLine:^(NSString *offlineStatus) {
        self.title = offlineStatus;
    }];
}

@end
