//
//  TKCTUserListHeaderView.m
//  EduClass
//
//  Created by talkcloud on 2018/10/15.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTUserListHeaderView.h"
#import "TKEduSessionHandle.h"

#define ThemeKP(args) [@"TKUserListView." stringByAppendingString:args]
@interface TKCTUserListHeaderView()

@property (strong, nonatomic) UILabel *equipmentLabel;
@property (strong, nonatomic) UILabel *nickNameLabel;
@property (strong, nonatomic) UILabel *underplatformLabel;
@property (strong, nonatomic) UILabel *videoLabel;
@property (strong, nonatomic) UILabel *audioLabel;

@property (strong, nonatomic) UILabel *editLabel;
@property (strong, nonatomic) UILabel *handLabel;
@property (strong, nonatomic) UILabel *bannedLabel;
@property (strong, nonatomic) UILabel *deleteLabel;

@property (nonatomic, strong) NSMutableArray *labelArray;

@property (nonatomic, strong) NSArray *labelTitleArray;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *videoLabelWidth;
@end

@implementation TKCTUserListHeaderView

- (instancetype)init
{
    if (self = [super init]) {
        self.equipmentLabel = [[UILabel alloc] init];
        self.nickNameLabel = [[UILabel alloc] init];
        self.underplatformLabel = [[UILabel alloc] init];
        self.videoLabel = [[UILabel alloc] init];
        self.audioLabel = [[UILabel alloc] init];
        self.editLabel = [[UILabel alloc] init];
        self.handLabel = [[UILabel alloc] init];
        self.bannedLabel = [[UILabel alloc] init];
        self.deleteLabel = [[UILabel alloc] init];
        [self addSubview:self.equipmentLabel];
        [self addSubview:self.nickNameLabel];
        [self addSubview:self.underplatformLabel];
        [self addSubview:self.videoLabel];
        [self addSubview:self.audioLabel];
        [self addSubview:self.editLabel];
        [self addSubview:self.handLabel];
        [self addSubview:self.bannedLabel];
        [self addSubview:self.deleteLabel];
        self.labelArray = [@[self.equipmentLabel,self.nickNameLabel,self.underplatformLabel,self.videoLabel,self.audioLabel,self.editLabel,self.handLabel,self.bannedLabel,self.deleteLabel] mutableCopy];
        self.labelTitleArray = @[TKMTLocalized(@"Label.Equipment"),TKMTLocalized(@"Label.UserNickname"),TKMTLocalized(@"Label.StepUpAndDown"),TKMTLocalized(@"Label.Camera"),TKMTLocalized(@"Label.Microphone"),TKMTLocalized(@"Label.Authorized"),TKMTLocalized(@"Label.RaisingHands"),TKMTLocalized(@"Label.Ban"),TKMTLocalized(@"Label.Remove")];
        
        if ([TKEduSessionHandle shareInstance].isOnlyAudioRoom) {
            [self.labelArray removeObject:self.videoLabel];
        }
        
        [self.labelArray enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            ((UILabel *)obj).textAlignment = NSTextAlignmentCenter;
        }];
        
        [self.labelArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:5 leadSpacing:0 tailSpacing:0];
        [self.labelArray mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.mas_centerY);
            make.height.equalTo(self.mas_height);
        }];
        
        self.deleteLabel.hidden = [TKEduClassRoom shareInstance].roomJson.configuration.isHiddenKickOutStudentBtn;
        
        self.underplatformLabel.hidden =
        self.videoLabel.hidden =
        self.audioLabel.hidden =
        self.editLabel.hidden =
        self.handLabel.hidden =
        self.bannedLabel.hidden =
        self.deleteLabel.hidden =
        [TKRoomManager instance].localUser.role == TKUserType_Patrol;
    }
    
    return self;
}

- (void)setTitleHeight:(CGFloat)height{
    
    CGFloat textH = IS_PAD ? 14 : 12;
    
    for (int i = 0; i<self.labelArray.count; i++) {
        UILabel *label = (UILabel *)self.labelArray[i];
        
        label.font = [UIFont systemFontOfSize:textH];
        label.sakura.textColor(ThemeKP(@"userlistTextColor"));
        label.text  = self.labelTitleArray[i];
        
    }
}

@end
