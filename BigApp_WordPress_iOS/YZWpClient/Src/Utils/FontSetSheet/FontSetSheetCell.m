//
//  FontSetSheetCell.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/11.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "FontSetSheetCell.h"
#import "UIImage+SNAdditions.h"
#import "UIViewExt.h"
#import "BuildConfig.h"
#import "Config.h"

@implementation FontSetSheetCell

@synthesize hadObserver;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
        
        
        
        
        leftView = [[UIImageView alloc]init];
        InfoLabel = [[UILabel alloc]init];
        InfoLabel.backgroundColor = [UIColor blackColor];
        InfoLabel.font = [UIFont fontWithName:kChineseFontNameXi size:16];
        InfoLabel.textAlignment = NSTextAlignmentCenter;
        [self.contentView addSubview:leftView];
//        [self.contentView addSubview:InfoLabel];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        self.imageView.image = [UIImage imageNamed:@"font_icon"];
        self.textLabel.text = LocalizedString(@"FONT_SIZE");
        
        UIImageView *_line = [[UIImageView alloc] initWithImage:[UIImage streImageNamed:@"cell_separatory_line"]];
        _line.width = kSCREEN_WIDTH;
        
        [self.contentView addSubview:_line];
        NSArray *array=@[LocalizedString(@"LITTLE_FONT"),LocalizedString(@"MIDDLE_FONT"),LocalizedString(@"LARGE_FONT"),LocalizedString(@"SUPPER_LARGE_FONT")];
        
        _segmentControl=[[UISegmentedControl alloc]initWithItems:array];
        _segmentControl.frame=CGRectMake(kSCREEN_WIDTH- AutoDeviceWidth(200)-10, 7, AutoDeviceWidth(200), 30);
        _segmentControl.selectedSegmentIndex = 0;
        _segmentControl.tintColor= RGBCOLOR(49, 145, 213);
        
        [self.contentView addSubview:_segmentControl];
        
//        _segmentControl.backgroundColor = [UIColor lightGrayColor];
        
//        UIFont* font = [UIFont fontWithName:kChineseFontNameXi size:17.0];
//        NSDictionary* textAttributes = @{NSFontAttributeName:font,
//                                         NSForegroundColorAttributeName:kNavgationTitleColor};
//        [segmentControl setTitleTextAttributes:textAttributes forState:UIControlStateNormal];
        
        

        [self configureViews];
        [self regitserAsObserver];
        self.hadObserver = YES;

    }
    return self;
}

-(void)layoutSubviews{
    [super layoutSubviews];
    leftView.frame = CGRectMake(20, (self.frame.size.height-20)/2, 20, 20);
//    InfoLabel.frame = CGRectMake(leftView.frame.size.width+leftView.frame.origin.x+15, (self.frame.size.height-20)/2, 140, 20);
    InfoLabel.frame = CGRectMake(0, 0, self.width, self.height);
    
    NSDictionary *textSizeAdjust = @{@"90":@0,
                                     @"100":@1,
                                     @"110":@2,
                                     @"120":@3,
                                     
                                 
                                 };

    _segmentControl.selectedSegmentIndex = [[textSizeAdjust objectForKey:[Config getFontSize]] integerValue];
    
}

-(void)setData:(FontSetSheetModel *)dicdata{
    cellData = dicdata;
    leftView.image = [UIImage imageNamed:dicdata.icon];
    InfoLabel.text = dicdata.title;
    if (dicdata.color) {
        InfoLabel.textColor = dicdata.color;
    }else{
         InfoLabel.textColor = RGBCOLOR(70, 70, 70);
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
    if(selected){
        leftView.image = [UIImage imageNamed:cellData.icon_on];
       
    }else{
        leftView.image = [UIImage imageNamed:cellData.icon];
    }
    // Configure the view for the selected state
}

- (void)dealloc
{
    if (self.hadObserver) {
        [self unregisterAsObserver];
    }
}


- (void)configureViews
{

    _segmentControl.tintColor = [[ThemeManager sharedInstance] colorWithColorName:kTmDetailSegmentColor];
    
    [self setBackgroundColor:[[ThemeManager sharedInstance] colorWithColorName:kTmDetailCellBackgroudColor]];
    [self.textLabel setTextColor:[[ThemeManager sharedInstance] colorWithColorName:kTmDetailCellTextColor]];
}

- (void)regitserAsObserver
{
    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
    [center addObserver:self
               selector:@selector(configureViews)
                   name:ThemeDidChangeNotification
                 object:nil];
}

- (void)unregisterAsObserver
{
    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
    [center removeObserver:self];
}

@end

