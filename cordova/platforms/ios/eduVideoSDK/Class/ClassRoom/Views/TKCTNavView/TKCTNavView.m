//
//  TKCTNavView.m
//  EduClass
//
//  Created by talkcloud on 2018/10/10.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTNavView.h"
#import "TKEduSessionHandle.h"
#import "TKDocmentDocModel.h"
#import "TKCTUploadView.h"
#import "TKIPhoneTypeString.h"
#import "TKEyeCareManage.h"
#import "TKHUD.h"

#define ThemeKP(args) [@"ClassRoom.TKNavView." stringByAppendingString:args]

@interface TKCTNavView ()
{
    // 上下课 按钮
    CGFloat beginBtnY;
    CGFloat buttonSpace;
    
    CGFloat beginAndEndButtonHight;
    CGFloat beginAndEndButtonWidth;
    
    // 其他按钮
    CGFloat button_W_H;
    
    UIImageView *_handsupTipsImageView;
}
@property (nonatomic, strong) NSDictionary	*aParamDic;

@property (nonatomic, strong) UIButton		*leaveButton;//退出
@property (nonatomic, strong) UIButton		*handButton;//举手按钮（未开启视频状态）
@property (nonatomic, strong) UIButton		*handHasVideoButton;//举手按钮（开启视频状态）
@property (nonatomic, strong) UIButton      *eyeCoverBtn;  // 护眼模式
@property (nonatomic, strong) UIButton		*cameraSwitchBtn; // 前后摄像头切换
@property (nonatomic, strong) UIButton		*styleButton; //布局

@property (nonatomic, strong) TKCTUploadView *uploadView;//文档上传视图（拍摄上传、相册上传）

@property (nonatomic, strong) UILabel *classTimerLabel;//时间显示
@property (nonatomic, strong) UILabel *titleLabel;//时间显示

@property (nonatomic, assign) CGFloat whiteBoardDocCurPage;

@property (nonatomic, assign) TKUIRoomType roomType;

@property (nonatomic, assign) BOOL isCanRaiseHandUp;//是否可以举手
@property (nonatomic, assign) BOOL isFrontCamera;
@property (nonatomic, assign) BOOL isNeedCameraBtn;
@property (nonatomic, assign) BOOL isNeedEyeCoverBtn;
@end

@implementation TKCTNavView


- (instancetype)initWithFrame:(CGRect)frame aParamDic:(NSDictionary *)aParamDic {
    
    if (self = [super initWithFrame:frame]) {
        
        // 全屏通知
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(whiteBoardFullScreen:) name:sChangeWebPageFullScreen object:nil];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(publishStatesUpdate:) name:[NSString stringWithFormat:@"%@%@",sRaisehand,[TKEduSessionHandle shareInstance].localUser.peerID] object:nil];
        
        _roomType = [TKEduClassRoom shareInstance].roomJson.roomtype;
        
        _isFrontCamera = YES;
        _aParamDic = aParamDic;
        beginBtnY = 7;
        buttonSpace = 20;
        
        beginAndEndButtonHight = CGRectGetHeight(self.frame)-beginBtnY*2;
        beginAndEndButtonWidth = beginAndEndButtonHight * 3;
        
        button_W_H = beginAndEndButtonHight - 4;
        
        _isNeedEyeCoverBtn = [TKEduClassRoom shareInstance].roomJson.roomrole == TKUserType_Student;
        
        _isNeedCameraBtn = [TKEduClassRoom shareInstance].roomJson.roomrole == TKUserType_Student ||
        [TKEduClassRoom shareInstance].roomJson.roomrole == TKUserType_Teacher;
        
        self.sakura.backgroundColor(ThemeKP(@"navgationColor"));
        
        
        // 离开按钮
        _leaveButton = ({
            UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
            
            btn.frame = CGRectMake([TKUtil IS_IPHONEX] ? 44 : 0, 0, CGRectGetHeight(self.frame), CGRectGetHeight(self.frame));
            btn.backgroundColor = [UIColor clearColor];
            btn.sakura.image(ThemeKP(@"common_icon_return"),UIControlStateNormal);
            [btn addTarget:self action:@selector(leaveClass:) forControlEvents:(UIControlEventTouchUpInside)];
            [self addSubview:btn];
            btn;
        });
        
        
        
        _classTimerLabel = [[UILabel alloc] init];
        [self addSubview:_classTimerLabel];
        _classTimerLabel.frame = CGRectMake(_leaveButton.rightX + 20, 0, 100, CGRectGetHeight(self.leaveButton.frame));
        _classTimerLabel.sakura.textColor(ThemeKP(@"titleColor"));
        _classTimerLabel.textAlignment = NSTextAlignmentLeft;
        [self setTime:0];// 显示时间为00:00:00
        
        // 网络状态
        if (!([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student &&
              [TKEduClassRoom shareInstance].roomJson.configuration.unShowStudentNetState)) {
            
            // 回放不显示网络
            if ([TKEduSessionHandle shareInstance].isPlayback == NO &&
                [TKEduSessionHandle shareInstance].localUser.role != TKUserType_Patrol) {
                
                _netTipView = [[TKCTNetTipView alloc] init];
                [self addSubview:_netTipView];
                [_netTipView mas_makeConstraints:^(MASConstraintMaker *make) {
                    make.left.equalTo(self.classTimerLabel.mas_right).offset(30 * Proportion);
                    make.centerY.equalTo(self.classTimerLabel);
                }];
                tk_weakify(self);
                _netTipView.netStateBlock = ^(BOOL isShow) {
                    if (weakSelf.netStateBlock) {
                        weakSelf.netStateBlock(weakSelf.netTipView.centerX);
                    }
                };
            }
        }
        
        CGFloat rightStartPoint = [TKUtil isiPhoneX] ? self.frame.size.width - 30 : self.frame.size.width - 20;
        
        
        //上课按钮
        _beginAndEndClassButton = ({
            
            UIButton *button = [UIButton buttonWithType:(UIButtonTypeCustom)];
            button.frame = CGRectMake(rightStartPoint - beginAndEndButtonWidth, beginBtnY, beginAndEndButtonWidth, beginAndEndButtonHight);
            
            
            button.backgroundColor = [UIColor clearColor];
            button.sakura.titleColor(ThemeKP(@"commom_btn_xiake_titleColor"),UIControlStateNormal);
            button.sakura.backgroundImage(ThemeKP(@"click_btn_xiakeImage"),  UIControlStateNormal);
            
            [button setTitle:TKMTLocalized(@"Button.ClassBegin1")  forState:UIControlStateNormal];
            [button setTitle:TKMTLocalized(@"Button.ClassIsOver1") forState:UIControlStateSelected];
            if (button.titleLabel.text) {
                int currentFontSize = [TKUtil getCurrentFontSize:CGSizeMake(button.frame.size.width, button.frame.size.height) withString:button.titleLabel.text];
                if (currentFontSize>15) {
                    currentFontSize = 15;
                }
                button.titleLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size:currentFontSize];
            }
            [button addTarget:self action:@selector(beginAndEndClassButtonClick:) forControlEvents:UIControlEventTouchUpInside];
            
            [self addSubview:button];
            button.userInteractionEnabled = NO;
            button;
        });
        
        
        
        
        //未开启视频状态下的举手按钮
        _handButton = ({
            UIButton * handButton = [UIButton buttonWithType:(UIButtonTypeCustom)];
            handButton.hidden = YES;
            handButton.frame = CGRectMake(_beginAndEndClassButton.leftX, beginBtnY, beginAndEndButtonWidth, beginAndEndButtonHight);
            handButton.sakura.titleColor(ThemeKP(@"commom_btn_xiake_titleColor"),UIControlStateNormal);
            [handButton setTitle:TKMTLocalized(@"Button.RaiseHand") forState:(UIControlStateNormal)];
            handButton.sakura.backgroundImage(ThemeKP(@"click_btn_xiakeImage"),UIControlStateNormal);
            handButton.sakura.backgroundImage(ThemeKP(@"click_btn_xiakeImage"),UIControlStateSelected);
            
            [handButton setTitle:TKMTLocalized(@"Button.CancleHandsup") forState:(UIControlStateSelected)];
            if (handButton.titleLabel.text) {
                int currentFontSize = [TKUtil getCurrentFontSize:CGSizeMake(handButton.frame.size.width, handButton.frame.size.height) withString:handButton.titleLabel.text];
                if (currentFontSize>15) {
                    currentFontSize = 15;
                }
                handButton.titleLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size:currentFontSize];
            }
            handButton.selected = NO;
            [handButton addTarget:self action:@selector(handButtonClick:) forControlEvents:(UIControlEventTouchUpInside)];
            
            [self addSubview:handButton];
            
            handButton;
        });
        
        //开启视频状态下的举手按钮
        _handHasVideoButton = ({
            UIButton * handButton = [UIButton buttonWithType:(UIButtonTypeCustom)];
            handButton.hidden = YES;
            handButton.frame = CGRectMake(_beginAndEndClassButton.leftX, beginBtnY, beginAndEndButtonWidth, beginAndEndButtonHight);
            handButton.sakura.titleColor(ThemeKP(@"commom_btn_xiake_titleColor"),UIControlStateNormal);
            [handButton setTitle:TKMTLocalized(@"Button.RaiseHand") forState:(UIControlStateNormal)];
            handButton.sakura.backgroundImage(ThemeKP(@"click_btn_xiakeImage"),UIControlStateNormal);
            if (handButton.titleLabel.text) {
                int currentFontSize = [TKUtil getCurrentFontSize:CGSizeMake(handButton.frame.size.width, handButton.frame.size.height) withString:handButton.titleLabel.text];
                if (currentFontSize>15) {
                    currentFontSize = 15;
                }
                handButton.titleLabel.font = [UIFont fontWithName:@"Helvetica-Bold" size:currentFontSize];
            }
            handButton.selected = NO;
            handButton.adjustsImageWhenHighlighted = NO;
            //处理按钮点击事件
            [handButton addTarget:self action:@selector(handTouchDown:)forControlEvents: UIControlEventTouchDown];
            //处理按钮松开状态
            [handButton addTarget:self action:@selector(handTouchUp:)forControlEvents: UIControlEventTouchUpInside | UIControlEventTouchUpOutside | UIControlEventTouchCancel];
            
            [self addSubview:handButton];
            handButton;
        });
        
        //学生：永远不显示上下课按钮
        //老师:一直显示上下课
        //巡视：( 配置项 巡课隐藏上下课按钮)YES 隐藏 ,  NO: 没上课 不显示上课按钮，若上课中显示下课按钮
        if ([TKEduClassRoom shareInstance].roomJson.roomrole == TKUserType_Teacher)
        {
            
            _beginAndEndClassButton.hidden	= NO;
            _handButton.hidden				= YES;
            _handHasVideoButton.hidden		= YES;
        }
        else
        {
            //学生
            if ([TKEduClassRoom shareInstance].roomJson.roomrole == TKUserType_Student)
            {
                _beginAndEndClassButton.hidden	= YES;
                _handButton.hidden 				= [TKEduSessionHandle shareInstance].isClassBegin ? NO : YES;
            }
            else // 巡课
            {
                _handButton.hidden				= YES;
                _beginAndEndClassButton.hidden	= YES;
            }
            
        }
        // 判断上下课按钮是否需要隐藏
        if ([TKEduClassRoom shareInstance].roomJson.configuration.hideClassBeginEndButton	== YES &&
            [TKEduClassRoom shareInstance].roomJson.configuration.autoStartClassFlag        == YES) {
            _beginAndEndClassButton.hidden    = YES;
        }
        
        if (_isNeedEyeCoverBtn) {
            _eyeCoverBtn = ({
                UIButton *button = [self returnButtonWithFrame:CGRectZero imageName:ThemeKP(@"button_eyeCover_default") selectImageName:ThemeKP(@"button_eyeCover_selected") action:@selector(eyeCoverButtonAction:) selected:NO];
                button;
            });
        }
        
        // 摄像头切换
        if (_isNeedCameraBtn) {
            _cameraSwitchBtn = ({
                UIButton *button = [self returnButtonWithFrame:CGRectZero imageName:ThemeKP(@"camera_btn") selectImageName:ThemeKP(@"camera_btn") action:@selector(swapFrontAndBackCameras) selected:NO];
                button;
            });
        }
        
        //布局按钮
        _styleButton = ({
            UIButton *button = [self returnButtonWithFrame:CGRectZero imageName:ThemeKP(@"tk_btn_buju_default") selectImageName:ThemeKP(@"tk_btn_buju_selected") action:@selector(styleButtonAction:) selected:NO];
            button;
        });
        
        // 上传按钮
        _upLoadButton = ({
            UIButton *button = [self returnButtonWithFrame:CGRectZero imageName:ThemeKP(@"common_camare_regular") selectImageName:ThemeKP(@"common_camare_selected") action:@selector(upLoadButtonClick:) selected:NO];
            button.hidden = YES;
            button;
        });
        
        
        //全体控制
        _controlButton = ({
            UIButton *button = [self returnButtonWithFrame:CGRectZero imageName:ThemeKP(@"button_control_default") selectImageName:ThemeKP(@"button_control_selected") action:@selector(controlButtonClick:) selected:NO];
            button;
        });
        
        //工具
        _toolBoxButton = ({
            UIButton *button = [self returnButtonWithFrame:CGRectZero imageName:ThemeKP(@"button_tools_default") selectImageName:ThemeKP(@"button_tools_selected") action:nil selected:NO];
            [button addTarget:self action:@selector(toolsButtonClick:) forControlEvents:(UIControlEventTouchUpInside)];
            button;
        });
        //课件库
        _coursewareButton = ({
            UIButton *button = [self returnButtonWithFrame:CGRectZero imageName:ThemeKP(@"button_courseware_default") selectImageName:ThemeKP(@"button_courseware_selected") action:@selector(coursewareButtonClick:) selected:NO];
            button;
        });
        
        // 成员
        NSString *iphoneTypeString = [TKIPhoneTypeString checkIPhoneType];
        
        _memberButton = ({
            UIButton *button = [self returnButtonWithFrame:CGRectZero imageName:ThemeKP(@"button_name_default") selectImageName:ThemeKP(@"button_name_selected") action:@selector(memberButtonClick:) selected:NO];
            button.showRedDot = NO;
            if ([iphoneTypeString containsString:@"iPhone 5"]) {
                button.redDotRadius = button.redDotRadius * 0.8;
                button.redDotOffset = CGPointMake(-5, 5);
            }else if ([iphoneTypeString containsString:@"iPad"]) {
                button.redDotRadius = button.redDotRadius * 1.2;
                button.redDotOffset = CGPointMake(-10, 12);
            }else {
                button.redDotOffset = CGPointMake(-6, 6);
            }
            
            button;
            
        });
        
        
        if([TKEduSessionHandle shareInstance].isPlayback){
            
            _classTimerLabel.hidden = YES;
            _beginAndEndClassButton.hidden = YES;
            
            //
            self.titleLabel.text = [TKEduClassRoom shareInstance].roomJson.roomname;
            self.titleLabel.frame = CGRectMake(_leaveButton.rightX + 20, 0, 500, CGRectGetHeight(self.frame));
            [self addSubview:self.titleLabel];
        }
    }
    
    // 是否显示 举手按钮
    [self isShowHandUpButton];
    
    return self;
}


- (void)showHandsupTips:(BOOL)show
{
    
    //老师才显示
    if (show && [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
        
        if (_handsupTipsImageView) {
            return;
        }
        //        float ratio = 87 / 34.0f;
        float handsup_height = self.height * 3 / 5;
        //        float handup_width = ratio * handup_height;
        
        CGSize textSize = [TKMTLocalized(@"Label.handsup_tips") boundingRectWithSize:CGSizeMake(CGFLOAT_MAX, handsup_height) options:NSStringDrawingTruncatesLastVisibleLine|NSStringDrawingUsesFontLeading|NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName : [UIFont systemFontOfSize:14]} context:nil].size;
        
        _handsupTipsImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, textSize.width + 32, handsup_height)];
        _handsupTipsImageView.centerY = self.centerY;
        _handsupTipsImageView.sakura.image(@"ClassRoom.TKNavView.handsup_tips");
        UIImage *image = _handsupTipsImageView.image;
        CGFloat top = image.size.height/2.0;
        CGFloat left = image.size.width/2.0;
        CGFloat bottom = image.size.height/2.0;
        CGFloat right = image.size.width/2.0;
        _handsupTipsImageView.image = [image resizableImageWithCapInsets:UIEdgeInsetsMake(top, left, bottom, right) resizingMode:UIImageResizingModeTile];
        
        _handsupTipsImageView.x = _memberButton.x - _handsupTipsImageView.width - 5;
        [self addSubview:_handsupTipsImageView];
        
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, textSize.width, _handsupTipsImageView.height)];
        label.textAlignment = NSTextAlignmentCenter;
        label.x = 15;
        label.centerY -= 2;//图片底部带阴影，-=保证居中看起来舒服
        label.sakura.textColor(@"ClassRoom.TKNavView.handsup_tips_color");
        label.text = TKMTLocalized(@"Label.handsup_tips");
        label.sakura.font(@"ClassRoom.TKNavView.handsup_tips_font");
        [_handsupTipsImageView addSubview:label];
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [UIView animateWithDuration:0.2f animations:^{
                _handsupTipsImageView.alpha = 0;
            } completion:^(BOOL finished) {
                [_handsupTipsImageView removeFromSuperview];
                _handsupTipsImageView = nil;
            }];
        });
    }
}

- (void)refreshUI:(BOOL)add{
    //设置上课或者举手按钮的背景图
    
    switch ([TKEduSessionHandle shareInstance].localUser.role) {
        case TKUserType_Teacher:
            
            if (add) {
                
                [_beginAndEndClassButton setTitle:TKMTLocalized(@"Button.ClassIsOver1") forState:(UIControlStateNormal)];
            }else{
                
                [_beginAndEndClassButton setTitle:TKMTLocalized(@"Button.ClassBegin1") forState:(UIControlStateNormal)];
            }
            
            break;
        case TKUserType_Student:
            
            // [_beginAndEndClassButton setTitle:MTLocalized(@"Button.RaiseHand") forState:(UIControlStateNormal)];
            
            if ([TKEduSessionHandle shareInstance].isClassBegin) {
                
                _handButton.hidden = NO;
                
            } else {
                _handButton.hidden = YES;
            }
            
            break;
        case TKUserType_Patrol:
            
            if (add && ![TKEduClassRoom shareInstance].roomJson.configuration.hideClassEndBtn) {
                _beginAndEndClassButton.hidden = NO;
                [_beginAndEndClassButton setTitle:TKMTLocalized(@"Button.ClassIsOver") forState:(UIControlStateNormal)];
            }else{
                
                _beginAndEndClassButton.hidden = YES;
            }
            _handButton.hidden = YES;
            _handHasVideoButton.hidden = YES;
            
            break;
            
        default:
            break;
    }
    
    [self isShowHandUpButton];
}

- (void)buttonRefreshUI {
    
    
    //按钮显示状态
    if ([TKEduSessionHandle shareInstance].isPlayback) {
        _eyeCoverBtn.hidden      = YES;
        _cameraSwitchBtn.hidden  = YES;
        _coursewareButton.hidden = YES;
        _memberButton.hidden     = YES;
        _styleButton.hidden      = YES;
        _toolBoxButton.hidden    = YES;
        _controlButton.hidden    = YES;
        
    }
    //巡课没有  资源库，课件，控制；
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        
        _toolBoxButton.hidden = YES;
        _controlButton.hidden = YES;
        _styleButton.hidden   = YES;
        _memberButton.hidden = NO;
        _coursewareButton.hidden = NO;
    }
    
    //如果角色是学生需隐藏一下按钮
    else if([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student){
        
        // 上课前学生可以查看课件库 上课后不允许， 学生不能看媒体库
        _toolBoxButton.hidden = YES;
        _controlButton.hidden = YES;
        _memberButton.hidden = YES;
        _styleButton.hidden = YES;
        
        // 无课件的不显示 课件库
        
        switch ([TKEduSessionHandle shareInstance].roomLayout) {
            case CoursewareDown:
            case VideoDown:
            case Encompassment:
            case Bilateral:
            case oneToOne:
            case oneToOneDoubleDivision:
            {
                if ([TKEduSessionHandle shareInstance].isClassBegin) {
                    
                    _coursewareButton.hidden = YES;
                    
                    NSDictionary *dic = [TKEduSessionHandle shareInstance].localUser.properties;
                    BOOL candrawDisable = ![TKUtil getBOOValueFromDic:dic Key:@"candraw"];
                    _upLoadButton.hidden = candrawDisable;
                    
                } else {
                    _coursewareButton.hidden = NO;
                    _upLoadButton.hidden = YES;
                }
                
                break;
            }
                
            case MainPeople:
            case OnlyVideo:
            case oneToOneDoubleVideo:
            {
                //不显示工具箱、控制按钮
                _coursewareButton.hidden = YES;
                _upLoadButton.hidden = YES;
                break;
            }
        }
    }
    //如果角色是老师
    else if([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher){
        
        // 没有白板的布局 不显示工具按钮
        // 一对一 的控制按钮只有 音频授课 不允许音频授课时 一对一不显示控制按钮
        
        _memberButton.hidden = NO;
        _coursewareButton.hidden = NO;
        _toolBoxButton.hidden = NO;
        _controlButton.hidden = NO;
        _styleButton.hidden = NO;
        
        // 无课件的不显示 工具和课件库
        switch ([TKEduSessionHandle shareInstance].roomLayout) {
            case CoursewareDown:
            case VideoDown:
            case Encompassment:
            case Bilateral:
            case oneToOne:
            case oneToOneDoubleDivision:
            {
                break;
            }
            case MainPeople:
            case OnlyVideo:
            case oneToOneDoubleVideo:
            {
                //不显示工具箱、控制按钮
                _toolBoxButton.hidden = YES;
                _coursewareButton.hidden = YES;
                break;
            }
        }
        
        if ([TKEduSessionHandle shareInstance].isClassBegin == YES )
        {
            if ([TKEduClassRoom shareInstance].roomJson.roomtype == TKRoomType_One &&
                ![TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish &&
                [TKEduClassRoom shareInstance].roomJson.configuration.canChangedToAudioOnly == NO) {
                _controlButton.hidden = YES;
            }
        }
        else
        {
            //未开始上课  不显示工具箱、控制按钮
            _toolBoxButton.hidden = YES;
            _controlButton.hidden = YES;
        }
    }
    
    
    CGFloat rightStartPoint = self.beginAndEndClassButton.leftX;
    if (self.beginAndEndClassButton.hidden && self.handButton.hidden && self.handHasVideoButton.hidden) {
        // 没有上下课 举手按钮 时 菜单按钮 从右开始
        rightStartPoint = self.beginAndEndClassButton.rightX + buttonSpace;
    }
    
    CGFloat buttonTop       = self.beginAndEndClassButton.centerY - button_W_H / 2;
    
    if (self.eyeCoverBtn && self.eyeCoverBtn.hidden == NO) {
        self.eyeCoverBtn.frame = CGRectMake(rightStartPoint - buttonSpace - button_W_H, buttonTop, button_W_H, button_W_H);
        rightStartPoint = self.eyeCoverBtn.leftX;
    }
    
    if (self.cameraSwitchBtn && self.cameraSwitchBtn.hidden == NO) {
        self.cameraSwitchBtn.frame = CGRectMake(rightStartPoint - buttonSpace - button_W_H, buttonTop, button_W_H, button_W_H);
        rightStartPoint = self.cameraSwitchBtn.leftX;
    }
    
    if (self.upLoadButton && self.upLoadButton.hidden == NO) {
        self.upLoadButton.frame = CGRectMake(rightStartPoint - buttonSpace - button_W_H, buttonTop, button_W_H, button_W_H);
        rightStartPoint = self.upLoadButton.leftX;
    } else {
        
        if (_uploadView) {
            [_uploadView dissMissView];
            _upLoadButton.selected = NO;
        }
    }
    
    if (self.styleButton && self.styleButton.hidden == NO) {
        self.styleButton.frame = CGRectMake(rightStartPoint - buttonSpace - button_W_H, buttonTop, button_W_H, button_W_H);
        rightStartPoint = self.styleButton.leftX;
    }
    
    if (self.controlButton && self.controlButton.hidden == NO) {
        self.controlButton.frame = CGRectMake(rightStartPoint - buttonSpace - button_W_H, buttonTop, button_W_H, button_W_H);
        rightStartPoint = self.controlButton.leftX;
    }
    
    if (self.toolBoxButton && self.toolBoxButton.hidden == NO) {
        self.toolBoxButton.frame = CGRectMake(rightStartPoint - buttonSpace - button_W_H, buttonTop, button_W_H, button_W_H);
        rightStartPoint = self.toolBoxButton.leftX;
    }
    
    if (self.coursewareButton && self.coursewareButton.hidden == NO) {
        self.coursewareButton.frame = CGRectMake(rightStartPoint - buttonSpace - button_W_H, buttonTop, button_W_H, button_W_H);
        rightStartPoint = self.coursewareButton.leftX;
    }
    
    if (self.memberButton && self.memberButton.hidden == NO) {
        self.memberButton.frame = CGRectMake(rightStartPoint - buttonSpace - button_W_H, buttonTop, button_W_H, button_W_H);
        rightStartPoint = self.memberButton.leftX;
    }
}

#pragma mark - 上下课逻辑
- (void)beginAndEndClassButtonClick:(UIButton *)sender{
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher ||
        [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        
        sender.selected = [TKEduSessionHandle shareInstance].isClassBegin;
        
        // 上课
        if (!sender.selected) {
            
            if([TKEduClassRoom shareInstance].roomJson.configuration.endClassTimeFlag) {
                
                [TKEduNetManager systemtime:self.aParamDic Complete:^int(id  _Nullable response) {
                    
                    if (response) {
                        int time =  [TKEduClassRoom shareInstance].roomJson.endtime - [response[@"time"] intValue];
                        //(2)未到下课时间： 老师未点下课->下课时间到->课程结束，一律离开
                        //(3)到下课时间->提前5分钟给出提示语（老师，助教）->课程结束，一律离开
                        if ((time >0 && time<=300) && [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
                            int ratio = time/60;
                            int remainder = time % 60;
                            
                            if (ratio == 0 && remainder>0) {
                                
                                [TKUtil showClassEndMessage:[NSString stringWithFormat:@"%d%@",remainder,TKMTLocalized(@"Prompt.ClassEndTimeseconds")]];
                            }else if(ratio>0){
                                
                                [TKUtil showClassEndMessage:[NSString stringWithFormat:@"%d%@",ratio,TKMTLocalized(@"Prompt.ClassEndTime")]];
                            }
                        }
                        
                        if (time<=0) {
                            [TKUtil showClassEndMessage:TKMTLocalized(@"FError.RoomDeletedOrExpired")];
                            return 0;
                        }
                        if([TKEduClassRoom shareInstance].roomJson.configuration.forbidLeaveClassFlag &&
                           [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher){
                            
                            [[TKEduSessionHandle shareInstance]sessionHandleDelMsg:sAllAll ID:sAllAll To:sTellNone Data:@{} completion:nil];
                        }
                        
                        [[TKEduSessionHandle shareInstance]configureHUD:@"" aIsShow:YES];
                        
                        // 如果是老师，上课前正在播放MP3或者MP4，点击上课按钮停止播放
                        if ([TKEduSessionHandle shareInstance].isPlayMedia && [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
                            [[TKEduSessionHandle shareInstance] sessionHandleUnpublishMedia:nil];
                        }
                        
                        
                        [TKEduNetManager classBeginStar:[TKEduClassRoom shareInstance].roomJson.roomid
                                              companyid:[TKEduClassRoom shareInstance].roomJson.companyid
                                                  aHost:sHost
                                                  aPort:sPort
                                                 userid:[TKEduSessionHandle shareInstance].localUser.peerID
                                                 roleid:[TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher ? @"0" : @"1"
                                              aComplete:^int(id  _Nullable response)
                         {
                             
                             [_beginAndEndClassButton setTitle:TKMTLocalized(@"Button.ClassIsOver") forState:UIControlStateNormal];
                             
                             NSString *str = [TKUtil dictionaryToJSONString:@{@"recordchat":@YES}];
                             
                             [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sClassBegin ID:sClassBegin To:sTellAll Data:str Save:true AssociatedMsgID:nil AssociatedUserID:nil expires:0 completion:nil];
                             
                             [[TKEduSessionHandle shareInstance]configureHUD:@"" aIsShow:NO];
                             
                             TKDocmentDocModel *docModel = [TKEduSessionHandle shareInstance].iCurrentDocmentModel;
                             [[TKEduSessionHandle shareInstance].whiteBoardManager showDocumentWithFile:(TKFileModel *)docModel isPubMsg:YES];
                             
                             return 0;
                         } aNetError:^int(id  _Nullable response) {
                             
                             [[TKEduSessionHandle shareInstance]configureHUD:@"" aIsShow:NO];
                             return 0;
                         }];
                    }
                    
                    
                    return 0;
                } aNetError:^int(id  _Nullable response) {
                    
                    return 0;
                }];
            }
            
            else {
                if([TKEduClassRoom shareInstance].roomJson.configuration.forbidLeaveClassFlag &&
                   [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
                    
                    [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sAllAll ID:sAllAll To:sTellNone Data:@{} completion:nil];
                    // 下课清理聊天日志
                    [[TKEduSessionHandle shareInstance] clearMessageList];
                    // 下课文档复位
                    [[TKEduSessionHandle shareInstance] fileListResetToDefault];
                    
                    [[TKEduSessionHandle shareInstance].whiteBoardManager showDocumentWithFile:(TKFileModel *)[[TKEduSessionHandle shareInstance] getClassOverDocument] isPubMsg:YES];
                }
                [[TKEduSessionHandle shareInstance]configureHUD:@"" aIsShow:YES];
                
                // 如果是老师，上课前正在播放MP3或者MP4，点击上课按钮停止播放
                if ([TKEduSessionHandle shareInstance].isPlayMedia &&
                    [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
                    
                    [[TKEduSessionHandle shareInstance] sessionHandleUnpublishMedia:nil];
                }
                
                [TKEduNetManager classBeginStar:[TKEduClassRoom shareInstance].roomJson.roomid
                                      companyid:[TKEduClassRoom shareInstance].roomJson.companyid
                                          aHost:sHost
                                          aPort:sPort
                                         userid:[TKEduSessionHandle shareInstance].localUser.peerID
                                         roleid:[TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher ? @"0" : @"1"aComplete:^int(id  _Nullable response)
                 {
                     
                     [_beginAndEndClassButton setTitle:TKMTLocalized(@"Button.ClassIsOver") forState:UIControlStateNormal];
                     
                     NSString *str = [TKUtil dictionaryToJSONString:@{@"recordchat":@YES}];
                     [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sClassBegin
                                                                          ID:sClassBegin
                                                                          To:sTellAll
                                                                        Data:str
                                                                        Save:true
                                                             AssociatedMsgID:nil
                                                            AssociatedUserID:nil
                                                                     expires:0
                                                                  completion:nil];
                     
                     [[TKEduSessionHandle shareInstance] configureHUD:@"" aIsShow:NO];
                     
                     TKDocmentDocModel *docModel = [TKEduSessionHandle shareInstance].iCurrentDocmentModel;
                     [[TKEduSessionHandle shareInstance].whiteBoardManager showDocumentWithFile:(TKFileModel *)docModel isPubMsg:YES];
                     return 0;
                 } aNetError:^int(id  _Nullable response) {
                     
                     [[TKEduSessionHandle shareInstance]configureHUD:@"" aIsShow:NO];
                     return 0;
                 }];
                
                
                
                if (self.classBeginBlock) {
                    self.classBeginBlock();
                }
                
            }
            
            
            
        }
        else {
            
            TKAlertView *alert = [[TKAlertView alloc]initForWarningWithTitle:TKMTLocalized(@"Prompt.prompt") contentText:TKMTLocalized(@"Prompt.FinishClass") leftTitle:TKMTLocalized(@"Prompt.Cancel") rightTitle:TKMTLocalized(@"Prompt.OK")];
            [alert show];
            alert.rightBlock = ^{
                
                _beginAndEndClassButton.selected = NO;
                
                [self setTime:0];
                
                // 下课关闭MP3和MP4
                if ([TKEduSessionHandle shareInstance].isPlayMedia == YES) {
                    [TKEduSessionHandle shareInstance].isPlayMedia = NO;
                    [[TKEduSessionHandle shareInstance] sessionHandleUnpublishMedia:nil];
                }
                
                [[TKEduSessionHandle shareInstance]configureHUD:@"" aIsShow:YES];
                
                [TKEduNetManager classBeginEnd:[TKEduClassRoom shareInstance].roomJson.roomid
                                     companyid:[TKEduClassRoom shareInstance].roomJson.companyid
                                         aHost:sHost
                                         aPort:sPort
                                        userid:[TKEduSessionHandle shareInstance].localUser.peerID
                                        roleid:[TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher ? @"0" : @"1"
                                     aComplete:^int(id  _Nullable response) {
                                         
                                         [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sClassBegin ID:sClassBegin To:sTellAll Data:@{} completion:nil];
                                         
                                         [[TKEduSessionHandle shareInstance] configureHUD:@"" aIsShow:NO];
                                         
                                         return 0;
                                     }aNetError:^int(id  _Nullable response) {
                                         
                                         [[TKEduSessionHandle shareInstance] configureHUD:@"" aIsShow:NO];
                                         
                                         return 0;
                                     }];
                if (self.classoverBlock) {
                    self.classoverBlock();
                }
            };
            alert.lelftBlock = ^{};
            
        }
        
        
    }
}
- (void)setShowRedDot:(BOOL)showRedDot {
    _memberButton.showRedDot = showRedDot;
}

#pragma mark - 举手
- (void)handButtonClick:(UIButton *)sender{
    TKLog(@"---举手1");
    
    // 在台上点击举手按钮无效，只响应长按
    if ([TKEduSessionHandle shareInstance].localUser.publishState > 0) {
        return;
    }
    
    sender.selected = ![[[TKEduSessionHandle shareInstance].localUser.properties objectForKey:sRaisehand] boolValue];
    [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:[TKEduSessionHandle shareInstance].localUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(sender.selected) completion:nil];
    
    
}
#pragma mark - 设置上台状态举手按钮的样式
- (void)setHandButtonState:(BOOL)isHandup{
    if (isHandup) {
        _handHasVideoButton.sakura.backgroundImage(ThemeKP(@"click_btn_raiseHand_image"),UIControlStateNormal);
        _handHasVideoButton.sakura.titleColor(ThemeKP(@"commom_btn_xiake_titleColor"),UIControlStateNormal);
        [_handHasVideoButton setTitle:TKMTLocalized(@"Button.RaiseHandCancle") forState:(UIControlStateNormal)];
    }else{
        
        _handHasVideoButton.sakura.titleColor(ThemeKP(@"commom_btn_xiake_titleColor"),UIControlStateNormal);
        [_handHasVideoButton setTitle:TKMTLocalized(@"Button.RaiseHand") forState:(UIControlStateNormal)];
        _handHasVideoButton.sakura.backgroundImage(ThemeKP(@"click_btn_xiakeImage"),UIControlStateNormal);
        
    }
    [self isShowHandUpButton];
}
#pragma mark - 举手中
- (void)handTouchDown:(UIButton *)sender{
    if ([TKEduClassRoom shareInstance].roomJson.roomrole != TKUserType_Student || [TKEduSessionHandle shareInstance].localUser.publishState == 0) {
        return;
    }
    [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:[TKEduSessionHandle shareInstance].localUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(YES) completion:nil];
    
}

#pragma mark - 取消举手
- (void)handTouchUp:(UIButton *)sender{
    
    if ([TKEduClassRoom shareInstance].roomJson.roomrole != TKUserType_Student || [TKEduSessionHandle shareInstance].localUser.publishState == 0) {
        return;
    }
    [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:[TKEduSessionHandle shareInstance].localUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(NO) completion:nil];
    
}

#pragma mark - 护眼模式开关
- (void)eyeCoverButtonAction:(UIButton *)sender {
    
    sender.selected = !sender.selected;
    [[TKEyeCareManage sharedUtil] switchEyeCareMode:sender.selected];
}

#pragma mark - 摄像头切换
- (void)swapFrontAndBackCameras {
    
    _isFrontCamera = !_isFrontCamera;
    [[TKEduSessionHandle shareInstance] sessionHandleSelectCameraPosition: _isFrontCamera];
}

#pragma mark - fullScreen 白板全屏
-(void)whiteBoardFullScreen:(NSNotification*)aNotification{
    
    bool isFull = [aNotification.object boolValue];
    if (isFull && _upLoadButton.selected) {
        _upLoadButton.selected = !_upLoadButton.selected;
        [self.uploadView dissMissView];
    }
}

#pragma mark - 学生上传信息
- (void) upLoadButtonClick:(UIButton *) sender {
    
    sender.selected = !sender.selected;
    if (sender.selected) {
        [self.uploadView showOnView:sender];
    }else{
        [self.uploadView dissMissView];
    }
}
- (TKCTUploadView *)uploadView{
    if (!_uploadView) {
        self.uploadView = [[TKCTUploadView alloc]init];
        __weak TKCTNavView * tab = self;
        
        self.uploadView.dismiss = ^{
            tab.upLoadButton.selected = NO;
        };
    }
    return _uploadView;
}

#pragma mark - 全体操作
- (void) controlButtonClick:(UIButton *) sender {
    
    sender.selected = !sender.selected;
    if (self.controlButtonClickBlock) {
        self.controlButtonClickBlock(sender);
    }
}

#pragma mark - 工具箱
- (void) toolsButtonClick:(UIButton *) sender {
    
    sender.selected = !sender.selected;
    if (self.toolBoxButtonClickBlock) {
        self.toolBoxButtonClickBlock(sender);
    }
}

#pragma mark - 课件库
- (void) coursewareButtonClick:(UIButton *) sender {
    
    sender.userInteractionEnabled = NO;
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5f * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        sender.userInteractionEnabled = YES;
    });
    
    sender.selected = !sender.selected;
    if (self.coursewareButtonClickBlock) {
        self.coursewareButtonClickBlock(sender);
    }
}

#pragma mark - 花名册
- (void) memberButtonClick:(UIButton *) sender {
    
    sender.userInteractionEnabled = NO;
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5f * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        sender.userInteractionEnabled = YES;
    });
    
    sender.selected = !sender.selected;
    if (sender.selected) {
        self.showRedDot = NO;
    }
    if (self.memberButtonClickBlock) {
        self.memberButtonClickBlock(sender);
    }
}
#pragma mark - 布局切换
- (void)styleButtonAction:(UIButton *)sender
{
    sender.selected = !sender.selected;
    if (self.styleButtonClickBlock) {
        self.styleButtonClickBlock(sender);
    }
}
#pragma mark - 离开课堂
- (void)leaveClass:(UIButton *)sender{
    
    if (self.leaveButtonBlock) {
        self.leaveButtonBlock();
    }
}

- (void)setTime:(NSTimeInterval)time{
    if (time / 1000000000 > 1) {
        // time 应该是 0～？？的秒 不应该是时间戳，如果出现时间戳说明还没上课
        _classTimerLabel.hidden = NO;
        _classTimerLabel.text = @" 00:00:00";
        return;
    }
    NSString * H = @"0";
    NSString * M = @"0";
    NSString * S = @"0";
    long temps = time;
    //long temps = 1;
    long tempm = temps / 60;
    long temph = tempm / 60;
    long sec = temps - tempm * 60;
    tempm = tempm - temph * 60;
    H = temph == 0 ? @"00" : temph >= 10 ? [NSString stringWithFormat:@"%@",@(temph)] : [NSString stringWithFormat:@"0%@",@(temph)];
    M = tempm == 0 ? @"00" : tempm >= 10 ? [NSString stringWithFormat:@"%@",@(tempm)] : [NSString stringWithFormat:@"0%@",@(tempm)];
    S = sec == 0 ? @"00" : sec >= 10 ? [NSString stringWithFormat:@"%@",@(sec)] : [NSString stringWithFormat:@"0%@",@(sec)];
    
    _classTimerLabel.hidden = NO;
    _classTimerLabel.text = [NSString stringWithFormat:@" %@:%@:%@",H,M,S];
}

#pragma mark - 接收视频状态通知
- (void)publishStatesUpdate:(NSNotification *)notification{
    
    if (![TKEduSessionHandle shareInstance].isClassBegin || [TKEduSessionHandle shareInstance].localUser.role != TKUserType_Student) {
        
        _handHasVideoButton.hidden = YES;
        _handButton.hidden = YES;
        
        [self buttonRefreshUI];
        return;
    }
    
    NSDictionary *tDic = (NSDictionary *)notification.object;
    
    PublishState tPublishState = (PublishState)[[tDic objectForKey:sPublishstate]integerValue];
    
    
    if (tPublishState == TKPublishStateNONE) {
        
        _handHasVideoButton.selected = NO;
        _handHasVideoButton.hidden = YES;
        _handButton.hidden = NO;
    }else{
        
        _handHasVideoButton.hidden = NO;
        
        _handButton.selected = NO;
        _handButton.hidden = YES;
    }
    
    [self isShowHandUpButton];
}

/*
 卡通模板（一对一）：不允许助教上台的教室，iPad学生进入教室 不应该有举手；
 如果是允许助教上台的一对一课堂，应该有举手；
 */
- (void)isShowHandUpButton {
    
    if(![TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish &&
       [TKEduClassRoom shareInstance].roomJson.roomtype == TKRoomTypeOneToOne ){
        
        _handButton.hidden = YES;
        _handHasVideoButton.hidden = YES;
        
    }
    // 回放
    if([TKEduSessionHandle shareInstance].isPlayback) {
        
        _handButton.hidden = YES;
        _handHasVideoButton.hidden = YES;
    }
    
    [self buttonRefreshUI];
}
- (void)updateView:(NSDictionary *)message{
    
    BOOL toolbox = [TKUtil getBOOValueFromDic:message Key:@"toolbox"];
    self.toolBoxButton.selected = toolbox;
}
- (void)hideAllButton:(BOOL)hide{
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
        
        
        _controlButton.hidden    =
        _coursewareButton.hidden =
        _memberButton.hidden     = hide;
        
        if ([TKEduSessionHandle shareInstance].isClassBegin == YES) {
            _toolBoxButton.hidden = hide;
        }
    }
    
}

#pragma mark - creat button
- (UIButton *)returnButtonWithFrame:(CGRect)frame imageName:(NSString *)imageName selectImageName:(NSString *)selectImageName action:(SEL)action selected:(BOOL)selected{
    
    UIButton *button = [UIButton buttonWithType:(UIButtonTypeCustom)];
    [self addSubview:button];
    if (NO == CGRectIsEmpty(frame)) {
        button.frame = frame;
    }
    button.imageView.contentMode = UIViewContentModeScaleAspectFit;
    
    if (imageName) {
        
        button.sakura.image(imageName,UIControlStateNormal);
    }
    if (selectImageName) {
        
        button.sakura.image(selectImageName,UIControlStateSelected);
    }
    if (action) {
        [button addTarget:self action:action forControlEvents:(UIControlEventTouchUpInside)];
    }
    
    return button;
}

- (void)dealloc{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)destory {
    
    if (self.uploadView) {
        [self.uploadView dissMissView];
    }
}

- (UILabel *)titleLabel
{
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.font = [UIFont systemFontOfSize:20];
        _titleLabel.sakura.textColor(ThemeKP(@"titleColor"));
    }
    return _titleLabel;
}

@end
