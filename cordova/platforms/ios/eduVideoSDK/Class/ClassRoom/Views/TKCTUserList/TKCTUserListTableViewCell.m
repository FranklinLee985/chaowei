//
//  TKCTUserListTableViewCell.m
//  EduClass
//
//  Created by talkcloud on 2018/10/15.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKCTUserListTableViewCell.h"
#import "TKEduSessionHandle.h"
#define ThemeKP(args) [@"TKUserListTableView." stringByAppendingString:args]

@interface TKCTUserListTableViewCell()
@property (strong, nonatomic) UIView *backView;
@property (strong, nonatomic) UIButton *removeBtn;//移除按钮
@property (strong, nonatomic) UIButton *bannedBtn;//禁言按钮
@property (strong, nonatomic) UIButton *handupBtn;//举手按钮
@property (strong, nonatomic) UIButton *editBtn;//授权按钮
@property (strong, nonatomic) UIButton *audioBtn;//麦克风按钮
@property (strong, nonatomic) UIButton *videoBtn;//摄像头按钮
@property (strong, nonatomic) UIButton *underPlatformBtn;//上下台按钮
@property (strong, nonatomic) UIImageView *iconImageView;//头像
@property (strong, nonatomic) UILabel *nameLabel;//用户名
@property (weak, nonatomic) NSLayoutConstraint *videoBtnWidth;
@end

@implementation TKCTUserListTableViewCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        
        self.backView = [[UIView alloc] init];
        [self addSubview:self.backView];
        [self.backView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left);
            make.right.equalTo(self.mas_right);
            make.top.equalTo(self.mas_top);
            make.bottom.equalTo(self.mas_bottom);
        }];
        
        self.iconImageView = [[UIImageView alloc] init];
        self.nameLabel = [[UILabel alloc] init];
        self.nameLabel.textAlignment = NSTextAlignmentCenter;
        self.underPlatformBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        self.videoBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        self.audioBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        self.editBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        self.handupBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        self.bannedBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        self.removeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        
        [self.underPlatformBtn addTarget:self action:@selector(underPlatform:) forControlEvents:UIControlEventTouchUpInside];
        [self.videoBtn addTarget:self action:@selector(videoClick:) forControlEvents:UIControlEventTouchUpInside];
        [self.audioBtn addTarget:self action:@selector(audioClick:) forControlEvents:UIControlEventTouchUpInside];
        [self.editBtn addTarget:self action:@selector(editClick:) forControlEvents:UIControlEventTouchUpInside];
        [self.bannedBtn addTarget:self action:@selector(bannedClick:) forControlEvents:UIControlEventTouchUpInside];
        [self.removeBtn addTarget:self action:@selector(removeClick:) forControlEvents:UIControlEventTouchUpInside];
        
        [self addSubview:self.iconImageView];
        [self addSubview:self.nameLabel];
        [self addSubview:self.underPlatformBtn];
        [self addSubview:self.videoBtn];
        [self addSubview:self.audioBtn];
        [self addSubview:self.editBtn];
        [self addSubview:self.handupBtn];
        [self addSubview:self.bannedBtn];
        [self addSubview:self.removeBtn];
        
        NSMutableArray *items = [@[self.iconImageView,
                           self.nameLabel,
                           self.underPlatformBtn,
                           self.videoBtn,
                           self.audioBtn,
                           self.editBtn,
                           self.handupBtn,
                           self.bannedBtn,
                           self.removeBtn] mutableCopy];
        if ([TKEduSessionHandle shareInstance].isOnlyAudioRoom) {
            [items removeObject:self.videoBtn];
        }
        
        [items mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:5 leadSpacing:0 tailSpacing:0];
        [items mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.mas_centerY);
            make.height.equalTo(@(21));
            make.width.equalTo(@(21));
        }];
        
        [self.iconImageView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.width.equalTo(@(35));
            make.height.equalTo(@(35));
        }];
        
        ////////
        self.backgroundColor = [UIColor clearColor];
        self.backView.layer.masksToBounds = YES;
        self.backView.layer.cornerRadius = 5;
        
        self.backView.sakura.backgroundColor(ThemeKP(@"listBackColor"));
        
        self.removeBtn.sakura.image(ThemeKP(@"button_remove"),UIControlStateNormal);
        self.removeBtn.hidden = [TKEduClassRoom shareInstance].roomJson.configuration.isHiddenKickOutStudentBtn;
        
        self.bannedBtn.sakura.image(ThemeKP(@"button_speak"),UIControlStateNormal);
        self.bannedBtn.sakura.image(ThemeKP(@"button_close_speak"),UIControlStateSelected);
        self.bannedBtn.sakura.image(ThemeKP(@"button_speak_unclickable"),UIControlStateDisabled);
        
        self.handupBtn.sakura.image(ThemeKP(@"icon_handup"),UIControlStateNormal);
        
        
        self.editBtn.sakura.image(ThemeKP(@"button_close_editor"),UIControlStateNormal);
        self.editBtn.sakura.image(ThemeKP(@"button_editor"),UIControlStateSelected);
        
        self.audioBtn.sakura.image(ThemeKP(@"button_close_audio"),UIControlStateNormal);
        self.audioBtn.sakura.image(ThemeKP(@"button_audio"),UIControlStateSelected);
        
        self.videoBtn.sakura.image(ThemeKP(@"button_close_video"),UIControlStateNormal);
        self.videoBtn.sakura.image(ThemeKP(@"button_video"),UIControlStateSelected);
        
        self.underPlatformBtn.sakura.image(ThemeKP(@"button_shangjiangtai"),UIControlStateNormal);
        self.underPlatformBtn.sakura.image(ThemeKP(@"button_xiajiangtai"),UIControlStateSelected);
        
        self.underPlatformBtn.imageView.contentMode =
        self.removeBtn.imageView.contentMode =
        self.videoBtn.imageView.contentMode =
        self.audioBtn.imageView.contentMode =
        self.editBtn.imageView.contentMode =
        self.bannedBtn.imageView.contentMode =
        self.handupBtn.imageView.contentMode =
        UIViewContentModeCenter;
        // Initialization code
        
        [self newUI];
        
    }
    
    return self;
}

- (void)newUI
{
    self.backView.layer.cornerRadius = 0;
    self.backView.backgroundColor = UIColor.clearColor;
    
    self.underPlatformBtn.hidden =
    self.removeBtn.hidden =
    self.videoBtn.hidden =
    self.audioBtn.hidden =
    self.editBtn.hidden =
    self.bannedBtn.hidden =
    self.handupBtn.hidden =
    [TKRoomManager instance].localUser.role == TKUserType_Patrol;
}

- (void)setRoomUser:(TKRoomUser *)roomUser{
    //通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshUserListUI:) name:[NSString stringWithFormat:@"%@%@",sRaisehand,roomUser.peerID] object:nil];
    
    _roomUser = roomUser;
    
    // 禁言
    BOOL disablechat = [TKUtil getBOOValueFromDic:roomUser.properties Key:sDisablechat];
    self.bannedBtn.selected = disablechat;
    if (_roomUser.role == TKUserType_Assistant) {
        _bannedBtn.selected = YES;
    }
    
    // 发布状态，0：未发布，1：发布音频；2：发布视频；3：发布音视频
    switch (roomUser.publishState) {
        case TKPublishStateNONE:
        {
            _underPlatformBtn.selected = NO;
            _videoBtn.selected = NO;
            _audioBtn.selected = NO;
            break;
        }
        case TKPublishStateAUDIOONLY:
        {
            
            _underPlatformBtn.selected = YES;
            _videoBtn.selected = NO ;
            _audioBtn.selected = YES;
            break;
        }
        case TKPublishStateVIDEOONLY:
        {
            
            _underPlatformBtn.selected = YES;
            _videoBtn.selected = YES;
            _audioBtn.selected = NO;
            break;
        }
        case TKPublishStateBOTH:
        {
            
            _underPlatformBtn.selected = YES;
            _videoBtn.selected = YES;
            _audioBtn.selected = YES;
        }
            break;
        case TKPublishStateNONEONSTAGE:
        {
            
            _underPlatformBtn.selected = YES;
            _videoBtn.selected = NO;
            _audioBtn.selected = NO;
        }
            break;
            
        default:
        {
            
            _underPlatformBtn.selected = NO;
            _videoBtn.selected = NO;
            _audioBtn.selected = NO;
            
        }
            break;
    }
    
    // 未上课 || （不是学生&& 不是助教） || （是助教&&不允许助教上台）
    if (![TKEduSessionHandle shareInstance].isClassBegin ||
        ((roomUser.role != TKUserType_Student) && (roomUser.role != TKUserType_Assistant) )||
        ((roomUser.role == TKUserType_Assistant) &&[TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish == NO)) {
        
        _underPlatformBtn.enabled = NO;
        _videoBtn.enabled = NO;
        _audioBtn.enabled = NO;
        _editBtn.enabled = NO;
        _handupBtn.hidden = YES;
        
    }else{

        _underPlatformBtn.enabled = YES;
        _videoBtn.enabled = YES;
        _audioBtn.enabled = YES;
        
        if (roomUser.role == TKUserType_Assistant) {
            _editBtn.selected = YES;
            _editBtn.enabled = NO;
        }else{
            _editBtn.enabled = YES;
            _editBtn.selected = roomUser.canDraw;
        }
        _handupBtn.hidden = ![[roomUser.properties objectForKey:sRaisehand]boolValue];
    }
    
    if (_roomUser.role == TKUserType_Assistant) {
        
        _bannedBtn.enabled = NO;
        _removeBtn.enabled = NO;
    } else {
        
        _bannedBtn.enabled = YES;
        _removeBtn.enabled = YES;
    }
    
    //fix bug:TALK-6542 助教不允许上台，助教不带斜杠（后续优化）
    if (roomUser.role == TKUserType_Assistant) {
        
        self.editBtn.sakura.image(ThemeKP(@"button_editor"),UIControlStateNormal);
    }else{
        self.editBtn.sakura.image(ThemeKP(@"button_close_editor"),UIControlStateNormal);
    }
    
    //用设备图标替换用户头像
    NSMutableDictionary *properties = roomUser.properties;
    NSString *devicetype = properties[@"devicetype"];
    
    _iconImageView.contentMode = UIViewContentModeScaleAspectFit;
    _iconImageView.sakura.image([TKHelperUtil returnDeviceImageName:devicetype]);
    
    // 当用被墙了，图标变化
    if ([roomUser.properties objectForKey:sUdpState]) {
        
        NSInteger udpState = [[roomUser.properties objectForKey:sUdpState] integerValue];
        if (udpState == 2) {
            
            _iconImageView.sakura.image([TKHelperUtil returnUDPDeviceImageName:devicetype]);
        } else {
            
            _iconImageView.sakura.image([TKHelperUtil returnDeviceImageName:devicetype]);
        }
    }
    
    //昵称 （身份）f
    NSAttributedString * attrStr =  [[NSAttributedString alloc]initWithData:[roomUser.nickName dataUsingEncoding:NSUTF8StringEncoding] options:@{NSDocumentTypeDocumentAttribute:NSHTMLTextDocumentType,NSCharacterEncodingDocumentAttribute: @(NSUTF8StringEncoding)} documentAttributes:nil error:nil];
    
    NSString *nickAndRole = [NSString stringWithFormat:@"%@",attrStr.string];
    _nameLabel.text = nickAndRole ;
    _nameLabel.lineBreakMode = NSLineBreakByTruncatingTail;
    _nameLabel.sakura.textColor(ThemeKP(@"coursewareButtonWhiteColor"));
}

-(void)configaration:(id)aModel withFileListType:(TKFileListType)aFileListType isClassBegin:(BOOL)isClassBegin{
    
    TKRoomUser *tRoomUser =(TKRoomUser *) aModel;
    
    // 发布状态，0：未发布，1：发布音频；2：发布视频；3：发布音视频
    switch (tRoomUser.publishState) {
        case TKPublishStateNONE:
        {
            _videoBtn.selected = NO;
            _videoBtn.selected = NO;
            break;
        }
        case TKPublishStateAUDIOONLY:
        {
            _videoBtn.selected = NO ;
            _audioBtn.selected = YES;
            break;
        }
        case TKPublishStateVIDEOONLY:
        {
            _videoBtn.selected = YES;
            _audioBtn.selected = NO;
            break;
        }
        case TKPublishStateBOTH:
        {
            _videoBtn.selected = YES;
            _audioBtn.selected = YES;
        }
            break;
            
        default:
            break;
    }
    
    
    if (!isClassBegin ||
        ((tRoomUser.role != TKUserType_Student) && (tRoomUser.role != TKUserType_Assistant) ) ||
        ((tRoomUser.role == TKUserType_Assistant) && [TKEduClassRoom shareInstance].roomJson.configuration.assistantCanPublish == NO)) {
        
        _handupBtn.hidden = YES;
        _videoBtn.hidden = YES;
        _underPlatformBtn.hidden = YES;
        _audioBtn.hidden = YES;

        
        _handupBtn.enabled = YES;
        _underPlatformBtn.enabled = YES;
        _audioBtn.enabled = YES;
        _underPlatformBtn.enabled = YES;

    }else{
        
        _handupBtn.hidden = ![[tRoomUser.properties objectForKey:sRaisehand]boolValue];
        _underPlatformBtn.hidden = NO;
        _audioBtn.hidden = NO;
        _editBtn.hidden = NO;
        
        if (tRoomUser.role == TKUserType_Assistant) {
            _editBtn.enabled = NO;
        }else{
            _editBtn.enabled = YES;
            _editBtn.selected = tRoomUser.canDraw;
        }
        
        _underPlatformBtn.enabled = YES;
        _audioBtn.enabled = YES;
        _videoBtn.enabled = YES;

    }
    
    //用设备图标替换用户头像
    NSMutableDictionary *properties = tRoomUser.properties;
    NSString *devicetype = properties[@"devicetype"];
    
    _iconImageView.contentMode = UIViewContentModeScaleAspectFit;
    _iconImageView.sakura.image([TKHelperUtil returnDeviceImageName:devicetype]);
    
    // 当用被墙了，图标变化
    
    
    if ([tRoomUser.properties objectForKey:sUdpState]) {
        
        NSInteger udpState = [[tRoomUser.properties objectForKey:sUdpState] integerValue];
        if (udpState == 2) {
            
            
            _iconImageView.sakura.image([TKHelperUtil returnUDPDeviceImageName:devicetype]);
            
        } else {
            _iconImageView.sakura.image([TKHelperUtil returnDeviceImageName:devicetype]);
            
            
        }
    }
    
    //昵称 （身份）f
    NSAttributedString * attrStr =  [[NSAttributedString alloc]initWithData:[tRoomUser.nickName dataUsingEncoding:NSUTF8StringEncoding] options:@{NSDocumentTypeDocumentAttribute:NSHTMLTextDocumentType,NSCharacterEncodingDocumentAttribute: @(NSUTF8StringEncoding)} documentAttributes:nil error:nil];
    
    NSString *nickAndRole = [NSString stringWithFormat:@"%@",attrStr.string];
    _nameLabel.text = nickAndRole ;
    _nameLabel.lineBreakMode = NSLineBreakByTruncatingTail;
    _nameLabel.sakura.textColor(ThemeKP(@"coursewareButtonDefaultColor"));
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    
    // Configure the view for the selected state
}

#pragma mark - 事件
//人员移除
- (IBAction)removeClick:(UIButton *)sender {
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        //训课不允许踢人
        return;
    }
    
    if (_roomUser.role == TKUserType_Assistant) {
        //不允许踢出助教
        return;
    }
    
    TKAlertView *alert = [[TKAlertView alloc]initForWarningWithTitle:TKMTLocalized(@"Prompt.prompt") contentText:TKMTLocalized(@"Prompt.KickOutStudent") leftTitle:TKMTLocalized(@"Prompt.Cancel") rightTitle:TKMTLocalized(@"Prompt.OK")];
    [alert show];
    alert.rightBlock = ^{
        
        [[TKEduSessionHandle shareInstance] sessionHandleEvictUser:_roomUser.peerID evictReason:@(1) completion:^(NSError *error) {
            
        }];
        
        if (self.delegate && [self.delegate respondsToSelector:@selector(removeblock)]) {
            [self.delegate removeblock];
        }
    };
    alert.lelftBlock = ^{
        
    };
    
}
//禁言功能
- (IBAction)bannedClick:(UIButton *)sender {
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        // 训课不允许操作禁言
        return;
    }
    
    if (_roomUser.role == TKUserType_Assistant) {
        //不允许禁言助教
        return;
    }
    
    sender.selected = !sender.selected;
    
    [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:_roomUser.peerID TellWhom:sTellAll Key:sDisablechat Value:@(sender.selected) completion:nil];
    
    
}
//授权功能
- (IBAction)editClick:(UIButton *)sender {
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    
    if (_roomUser.publishState==TKUser_PublishState_NONE && !_roomUser.canDraw && ![self theNumberOfTransfinite]) {

        [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateNONEONSTAGE completion:nil];
    }
    
    [[TKEduSessionHandle shareInstance]configureDraw:!_roomUser.canDraw isSend:YES to:sTellAll peerID:_roomUser.peerID];
}
//音频控制
- (IBAction)audioClick:(UIButton *)sender {
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    
    if (![_roomUser.peerID isEqualToString:@""]) {
        
        if (_roomUser.publishState == TKPublishStateNONE) {
            
            // 人数超限提醒
            if (![self theNumberOfTransfinite]) {
                
                [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateAUDIOONLY completion:nil];
                [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:_roomUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(false) completion:nil];
            }
            
        }else if (_roomUser.publishState == TKPublishStateAUDIOONLY){
            // 该状态下，音视频都关闭但在台上
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateNONEONSTAGE completion:nil];
            
        }else if (_roomUser.publishState == TKPublishStateBOTH){
            
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateVIDEOONLY completion:nil];
            
        }else if(_roomUser.publishState == TKPublishStateVIDEOONLY){
            
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateBOTH completion:nil];
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:_roomUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(false) completion:nil];
            
        } else if (_roomUser.publishState == (TKPublishState)TKPublishStateNONEONSTAGE) {
            
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateAUDIOONLY completion:nil];
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:_roomUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(false) completion:nil];
        }
    }
}
//视频控制
- (IBAction)videoClick:(UIButton *)sender {
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    
    if (![_roomUser.peerID isEqualToString:@""]) {
        
        if (_roomUser.publishState == TKPublishStateNONE) {
            
            if (![self theNumberOfTransfinite]) {// 人数超限提醒
                
                [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateVIDEOONLY completion:nil];
                [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:_roomUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(false) completion:nil];
            }
            
        }else if (_roomUser.publishState == TKPublishStateVIDEOONLY){
            // 这种情况下音视频都关闭，但还在台上
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateNONEONSTAGE completion:nil];
            
        }else if (_roomUser.publishState == TKPublishStateBOTH){
            
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateAUDIOONLY completion:nil];
            
        }else if(_roomUser.publishState == TKPublishStateAUDIOONLY){
            
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateBOTH completion:nil];
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:_roomUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(false) completion:nil];
        } else if (_roomUser.publishState == (TKPublishState) TKPublishStateNONEONSTAGE) {
            
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateVIDEOONLY completion:nil];
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:_roomUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(false) completion:nil];
        }
    }
}
//上下台
- (IBAction)underPlatform:(UIButton *)sender {
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;
    }
    
    // 防止点击过于频繁(点击次数超过10次，点击时间间隔大于0.8s)
    if ([TKEduSessionHandle shareInstance].onPlatformClickTimes && [TKEduSessionHandle shareInstance].onPlatformClickTimes > 0) {
        [TKEduSessionHandle shareInstance].onPlatformClickTimes++;
    } else {
        [TKEduSessionHandle shareInstance].onPlatformClickTimes = 1;
    }
    
    id idTime = [[NSUserDefaults standardUserDefaults] objectForKey:TKUnderPlatformTime];
    if (idTime && [idTime isKindOfClass:NSDate.class]) {
        NSDate *time = (NSDate *)idTime;
        NSDate *curTime = [NSDate date];
        NSTimeInterval delta = [curTime timeIntervalSinceDate:time]; // 计算出相差多少秒
        
        if (delta < 0.8 && [TKEduSessionHandle shareInstance].onPlatformClickTimes >= 10) {
            [TKUtil showMessage:TKMTLocalized(@"Prompt.underPlatformTime")];
            
            [TKEduSessionHandle shareInstance].onPlatformClickTimes = 0;
            
            return;
        } else {
            [[NSUserDefaults standardUserDefaults] removeObjectForKey:TKUnderPlatformTime];
        }
    }
    [[NSUserDefaults standardUserDefaults] setObject:[NSDate date] forKey:TKUnderPlatformTime];
    
    if (_roomUser.publishState == TKUser_PublishState_NONE || _roomUser.publishState == TKUser_PublishState_UNKown) {
        
        if (![self theNumberOfTransfinite]) {// 人数超限提醒
            
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateBOTH completion:nil];
            
            //上台时通知该学生更改举手状态为未举手
            [[TKEduSessionHandle shareInstance] sessionHandleChangeUserProperty:_roomUser.peerID TellWhom:sTellAll Key:sRaisehand Value:@(false) completion:nil];
        }
        
    } else {
        
        [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPublish:_roomUser.peerID Publish:TKPublishStateNONE completion:nil];
        // 助教始终有画笔权限
        if (_roomUser.role != TKUserType_Assistant) {
            [[TKEduSessionHandle shareInstance]configureDraw:false isSend:true to:sTellAll peerID:_roomUser.peerID];
        }
    }
}

#pragma mark - 人数超限提醒
- (BOOL)theNumberOfTransfinite {
    NSInteger maxVIdeo = [[TKEduClassRoom shareInstance].roomJson.maxvideo integerValue];
    if ([TKEduSessionHandle shareInstance].onPlatformNum == maxVIdeo && _roomUser.publishState == TKPublishStateNONE) {
        
        TKAlertView *alter = [[TKAlertView alloc] initWithTitle:@"提示" contentText:@"教室发言人数超过限制" confirmTitle:@"确定"];
        [alter show];
        return YES;
    }
    return NO;
}


- (void)refreshUserListUI:(NSNotification *)aNotification{
    
    //打开视频关闭视频开关
    NSDictionary *dict = (NSDictionary *)aNotification.object;
    TKRoomUser *user = (TKRoomUser*)[dict objectForKey:sUser];
    
    if (![user.peerID isEqualToString:_roomUser.peerID]) {
        return;
    }
    
    if ([dict objectForKey:sPublishstate]) {
        TKPublishState tPublishState = (TKPublishState)[[dict objectForKey:sPublishstate]integerValue];
        BOOL tAudioImageShow = (tPublishState  == TKPublishStateBOTH || tPublishState == TKPublishStateAUDIOONLY );
        //todo
        _audioBtn.selected = tAudioImageShow;
        BOOL turnOffCameraShow = (tPublishState == TKPublishStateBOTH || tPublishState == TKPublishStateVIDEOONLY ||tPublishState == (TKPublishState)TKPublishStateLocalNONE);
        
        _videoBtn.selected = turnOffCameraShow;
        
        BOOL tunder = (tPublishState == TKPublishStateNONE);
        
        _underPlatformBtn.selected = !tunder;
    }
    
    if ([dict objectForKey:sRaisehand]) {
        BOOL tHandsUpImageShow = (![[dict objectForKey:sRaisehand]boolValue]);
        _handupBtn.hidden = tHandsUpImageShow;
        
    }
    
    if ([dict objectForKey:sCandraw]) {
        
        BOOL tDrawImageShow = [[dict objectForKey:sCandraw]boolValue];
        if (user.role == TKUserType_Assistant) {
            return;
        }
        _editBtn.selected = tDrawImageShow;
    }
    
}
- (void)dealloc{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
}
@end
