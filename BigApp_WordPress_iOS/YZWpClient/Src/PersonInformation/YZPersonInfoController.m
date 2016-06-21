//
//  YZPersonInfoController.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/14.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZPersonInfoController.h"
#import "Config.h"
#import "UIViewExt.h"
#import "UIImage+SNAdditions.h"
#import "UIImageView+AFNetworking.h"

#import "UIViewController+YZProgressHUD.h"

#import "YZModifyNicknameController.h"
#import "YZModifyInfoController.h"
#import "YZModifyPwdViewController.m"

#import "YZPersonInfoCell.h"

#import "YZHttpClient.h"


#define kPersonInfoCellIdentifier @"PersonCellIdentifier"

@interface YZPersonInfoController ()
{
    NSArray                 *titleArray;
    YZLoginModel            *userModel;
    NSArray                 *seconIconArray;
}

@property (strong,nonatomic)UIImageView *userIcon;

@end

@implementation YZPersonInfoController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.isBackBarButtonItemShow = YES;
    self.title = @"个人资料";
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStyleGrouped];
    self.tableView.backgroundColor = [UIColor colorWithRed:242.0/255 green:242.0/255 blue:242.0/255 alpha:1];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.showsVerticalScrollIndicator = NO;
    
    titleArray = @[@"头像",@"昵称",@"个人说明"];
//    seconIconArray = @[@"person_info_email"];
    seconIconArray = @[@"person_info_email",@"pwd_icon"];
    
    userModel = [Config loadLoginUser];
    
    [Config loadCookies];
}


- (UIImageView *)userIcon
{
    if (!_userIcon) {
        
        _userIcon = [[UIImageView alloc] initWithFrame:
                     CGRectMake(kSCREEN_WIDTH -90, 10, 60, 60)];
        _userIcon.image = [UIImage imageNamed:(@"default_user_icon")];
        _userIcon.layer.cornerRadius = _userIcon.frame.size.width/2;
        _userIcon.layer.masksToBounds = YES;
        _userIcon.contentMode = UIViewContentModeScaleAspectFill;
        _userIcon.userInteractionEnabled = YES;
        
    }
    return _userIcon;
}

#pragma mark - Table view data source

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 20;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 1;
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (section == 0) {

        return titleArray.count;
    }
    else if (section == 1)
    {
        return seconIconArray.count;
    }
    return 1;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            return 80;
        }
        else if (indexPath.row == 2)
        {
            return 80;
        }
        return 50;
    }
    return 50;
}

- (UITableViewCellStyle)cellStyleIndexPath:(NSInteger )introw
{
    UITableViewCellStyle style = UITableViewCellStyleDefault;
    switch (introw) {
        case 0:
            style = UITableViewCellStyleDefault;
            break;
        case 1:
            style = UITableViewCellStyleValue1;
            break;
        case 2:
            style = UITableViewCellStyleSubtitle;
            break;
            
        default:
            break;
    }
    return style;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *kCellIdentifier = @"cell_id";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kCellIdentifier];
    
    if (indexPath.section == 0) {
        if (indexPath.row == 2) {
            cell = [[YZPersonInfoCell alloc] initWithStyle:[self cellStyleIndexPath:indexPath.row] reuseIdentifier:kCellIdentifier];
        }
        else
        {
            cell = [[UITableViewCell alloc] initWithStyle:[self cellStyleIndexPath:indexPath.row] reuseIdentifier:kCellIdentifier];
        }
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    else
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:kCellIdentifier];
    }
    
    cell.backgroundColor = [UIColor whiteColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.textLabel.font = [UIFont systemFontOfSize:14];
    cell.textLabel.textColor = RGBACOLOR(40, 40, 40, 1);
    
    if (indexPath.section == 0) {
        cell.textLabel.text = titleArray[indexPath.row];
        if (indexPath.row == 0)
        {
            [cell.contentView addSubview:self.userIcon];
            [self.userIcon setImageWithURL:[NSURL URLWithString:userModel.avatar] placeholderImage:[UIImage imageNamed:@"default_user_icon"]];
        }
        else if (indexPath.row == 1) {
            cell.detailTextLabel.text = userModel.niceName;
            
        }
        else if (indexPath.row == 2)
        {
            cell.detailTextLabel.text = userModel.udescription;
        }
    }
    else if (indexPath.section == 1)
    {
        cell.imageView.image = [UIImage imageNamed:[seconIconArray objectAtIndex:indexPath.row]];
        
        if (indexPath.row == 0) {
            if (userModel.email == nil || [userModel.email isEqual:[NSNull null]] || [userModel.email isEqualToString:@""]) {
                cell.textLabel.text = @"邮箱";
            }
            else
            {
                cell.textLabel.text = userModel.email;
            }
            
        }
        else if (indexPath.row == 1)
        {
            cell.textLabel.text = @"修改密码";
        }
        
    }
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 0) {
        if (indexPath.row == 0) {
            //头像
//            [YZHttpClient uploadImage:nil];
            UIActionSheet *actionSheet = [[UIActionSheet alloc]
                                          initWithTitle:@"上传头像"
                                          delegate:self
                                          cancelButtonTitle:@"取消"
                                          destructiveButtonTitle:nil
                                          otherButtonTitles:@"拍照",@"从手机相册选择",nil];
            actionSheet.actionSheetStyle = UIActionSheetStyleBlackOpaque;
            [actionSheet showInView:self.view];
            
        }
        else if (indexPath.row == 1)
        {
            YZModifyNicknameController *nickName = [[YZModifyNicknameController alloc]init];
            nickName.nickNameStr = userModel.niceName;
            [self.navigationController pushViewController:nickName animated:YES];
            
            [nickName setDrawBackNickName:^(NSString *nickName){
                userModel.niceName = nickName;
                [Config saveLoginUser:userModel];
                [Config saveCookies];
                
                UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
                cell.detailTextLabel.text = nickName;
            }];
        }
        else if (indexPath.row == 2)
        {
            YZModifyInfoController *info = [[YZModifyInfoController alloc]init];
            
            info.infoStr = userModel.udescription;
            [self.navigationController pushViewController:info animated:YES];
            
            [info setDrawBackPersonInfo:^(NSString *infoStr){
                userModel.udescription = infoStr;
                [Config saveLoginUser:userModel];
                [Config saveCookies];
                
                UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
                cell.detailTextLabel.text = infoStr;
            }];
            
        }
    }
    else if (indexPath.section == 1)
    {
        
        if (indexPath.row == 1) {
            YZModifyPwdViewController *pwd = [[YZModifyPwdViewController alloc]init];
            [self.navigationController pushViewController:pwd animated:YES];
            
            [pwd setDrawBackPersonPwd:^(NSString *pwd){
                
            }];
        }
    }
}

#pragma mark 上传头像

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 0) {
        [self takePhone];
    }else if (buttonIndex == 1) {
        [self takeFromXiangce];
    }else if(buttonIndex == 2) {
//        [self hidenPhotoView];
    }
    
    
}


#pragma mark 相册／拍照
UIImagePickerController *takePhonepicker;
UIImagePickerController *xiangcepicker;
- (void)takePhone
{
    takePhonepicker = [[UIImagePickerController alloc] init];
    takePhonepicker.delegate = self;
    takePhonepicker.sourceType = UIImagePickerControllerSourceTypeCamera;
    takePhonepicker.modalTransitionStyle = UIModalTransitionStyleCoverVertical;
    takePhonepicker.allowsEditing = YES;
    takePhonepicker.showsCameraControls = YES;
    [self.navigationController presentViewController:takePhonepicker animated:YES completion:nil];
    
}
- (void)takeFromXiangce
{
    xiangcepicker = [[UIImagePickerController alloc] init];
    xiangcepicker.delegate = self;
    xiangcepicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    [self.navigationController presentViewController:xiangcepicker animated:YES completion:nil];
}



// 初始化选取
- (void)imagePickerController:(UIImagePickerController *)picker
        didFinishPickingImage:(UIImage *)image
                  editingInfo:(NSDictionary *)editingInfo
{
    
    //初始化imageNew为从相机中获得的--
    
    
    [YZHttpClient uploadImage:image returnInfo:^(NSInteger errorCode, NSString *imageUrl){
        if (errorCode == 0) {
            //上传成功
            userModel.avatar = imageUrl;
            [Config saveLoginUser:userModel];
            [self.userIcon setImageWithURL:[NSURL URLWithString:userModel.avatar] placeholderImage:[UIImage imageNamed:@"default_user_icon"]];
        }
        else
        {
            //imageurl是错误提示
            [self presentSheet:imageUrl ForView:self.view];
        }
        
    }];
    
    [picker dismissViewControllerAnimated:NO completion:nil];

    
}


// 完成选取
- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [picker dismissViewControllerAnimated:YES completion:nil];
    
}

#pragma mark end

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
