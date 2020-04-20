//
//  TKNewChatView.m
//  EduClass
//
//  Created by talk on 2018/11/22.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKNewChatView.h"
#import "TKNewMessageCell.h"
#import "TKNewChatMessageTableViewCell.h"
#import "TKNewPictureCell.h"
#import "TKChatToolView.h"

#define chatToolHeight 44

#define ThemeKP(args) [@"ClassRoom.TKChatViews." stringByAppendingString:args]
#define ThemeNavViewKP(args) [@"ClassRoom.TKNavView." stringByAppendingString:args]

@interface TKNewChatView ()
<UITableViewDelegate,
UITableViewDataSource,
TKChatToolViewDelegate,
UITextViewDelegate,
UIScrollViewDelegate>

{
    CGFloat _btnWidth;
    CGFloat _btnSpace;
    
    UIButton *_middleBtn;
    UIButton *_rightBtn;
    UIButton *_leftBtn;
    CAGradientLayer *_gLayer;
    
    UILabel *_badgeLabel;
    
    BOOL _shouldShowKeyboard;
}

@property (nonatomic, strong) NSTimer *chatTimer;
@property (nonatomic, assign) BOOL chatTimerFlag;
@property (nonatomic, strong) NSString *lastSendChatTime;
@property (nonatomic, assign) CGFloat keyboardHeight;
@property (nonatomic, assign) CGFloat keyboardViewHeight;
@property (nonatomic, strong) UITableView *iChatTableView; // 聊天tableView
@property (nonatomic, strong) NSArray<TKChatMessageModel *>  *iMessageList;//聊天列表
@property (nonatomic, assign) BOOL isCustomInputView;

@end

@implementation TKNewChatView

- (instancetype)initWithFrame:(CGRect)frame 
{
    if (self = [super initWithFrame:frame]) {
        
        self.backgroundColor = [UIColor clearColor];
        self.alpha = 1;
        
        _iMessageList = [[TKEduSessionHandle shareInstance] messageList];
        self.keyboardHeight = 0;
        
        [self loadNotification];
        
        [self newUI];
        [self reloadData];
    }
    return self;
}

- (void)newUI
{
    _btnWidth = 46;
    _btnSpace = 20;
    
    self.iChatTableView.alpha = 0;
    self.iChatTableView.frame = CGRectMake(0, 0, self.width, self.height - _btnWidth);
    self.iChatTableView.tableHeaderView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 0, 0.1f)];
    [self addSubview:self.iChatTableView];
    
    _rightBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _rightBtn.frame = CGRectMake(0, self.height - _btnWidth, _btnWidth, _btnWidth);
    _rightBtn.sakura.image(ThemeNavViewKP(@"button_message_onview_jinyan"), UIControlStateNormal);
    _rightBtn.sakura.image(ThemeNavViewKP(@"button_message_onview_yijinyan"), UIControlStateSelected);
    [_rightBtn addTarget:self action:@selector(shutUpAction:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:_rightBtn];
    
    _middleBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _middleBtn.frame = CGRectMake(0, self.height - _btnWidth, _btnWidth, _btnWidth);
    _middleBtn.sakura.image(ThemeNavViewKP(@"button_message_onview_input"), UIControlStateNormal);
    _middleBtn.sakura.image(ThemeNavViewKP(@"button_message_onview_input_disable"), UIControlStateSelected);
    
    [_middleBtn addTarget:self action:@selector(input) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:_middleBtn];
    
    _leftBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _leftBtn.frame = CGRectMake(0, self.height - _btnWidth, _btnWidth, _btnWidth);
    _leftBtn.sakura.image(ThemeNavViewKP(@"button_message_onview"), UIControlStateNormal);
    _leftBtn.sakura.image(ThemeNavViewKP(@"button_message_onview_selected"), UIControlStateSelected);
    [_leftBtn addTarget:self action:@selector(showMessageView:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:_leftBtn];
    
    _badgeLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(_leftBtn.frame) - 17, _leftBtn.y + 1, 16, 16)];
    _badgeLabel.layer.cornerRadius = _badgeLabel.width / 2;
    _badgeLabel.layer.masksToBounds = YES;
    _badgeLabel.font = TKFont(8);
    _badgeLabel.backgroundColor = UIColor.redColor;
    _badgeLabel.textColor = UIColor.whiteColor;
    _badgeLabel.textAlignment = NSTextAlignmentCenter;
    _badgeLabel.lineBreakMode = NSLineBreakByClipping;
    _badgeLabel.hidden = YES;
    [self addSubview:_badgeLabel];
    
    _middleBtn.alpha = _rightBtn.alpha = 0;
    
    //////////////////////////////////////////////////
    //巡课不允许发送聊天消息
    //隐藏中间聊天按钮
    _middleBtn.hidden = ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol);
    
    
    //////////////////添加渐变_gLayer//////////////////
    _gLayer = [CAGradientLayer layer];
    _gLayer.colors = @[(id)UIColor.whiteColor.CGColor,(id)UIColor.whiteColor.CGColor, (id)UIColor.clearColor];
    _gLayer.locations = @[@0.5f, @0.7f];
    _gLayer.startPoint = CGPointMake(0, 1);
    _gLayer.endPoint = CGPointMake(0, 0);
    
    _gLayer.anchorPoint = CGPointMake(0, 0);
    _gLayer.bounds = CGRectMake(0, 0, self.iChatTableView.width, self.iChatTableView.height);
    self.iChatTableView.layer.mask = _gLayer;
    
    if ([TKEduSessionHandle shareInstance].localUser.role != TKUserType_Teacher) {
        
        if ([TKUtil getBOOValueFromDic:[TKEduSessionHandle shareInstance].localUser.properties Key:sDisablechat]) {
            self.keyboardView.userInteractionEnabled = NO;
            self.keyboardView.inputField.placehoder = TKMTLocalized(@"Prompt.BanChat");
        }else{
            
            self.keyboardView.userInteractionEnabled = YES;
            self.keyboardView.inputField.placehoder = TKMTLocalized(@"Say.say");
        }
    }
}

// 全体禁言
- (void)shutUpAction:(UIButton *)btn {
    
    BOOL abool = !btn.selected;
    // 信令
    if (abool) {
        [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sEveryoneBanChat ID:sEveryoneBanChat To:sTellAll Data:@{} Save:true AssociatedMsgID:nil AssociatedUserID:nil expires:0 completion:nil];
    }
    else {
        [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sEveryoneBanChat ID:sEveryoneBanChat To:sTellAll Data:@{} completion:nil ];
    }
    
    NSDictionary *dict = @{sDisablechat: @(abool)};
    [[TKEduSessionHandle shareInstance] sessionHandleChangeUserPropertyByRole:@[@(TKUserType_Student)]
                                                                     tellWhom:sTellAll
                                                                     property:dict
                                                                   completion:nil];
    btn.selected = abool;
}

#pragma mark - 收到消息
- (void)messageReceived:(NSString *)message
                 fromID:(NSString *)peerID
              extension:(NSDictionary *)extension{
    
    NSString *tDataString = [NSString stringWithFormat:@"%@",message];
    NSData *tJsData = [tDataString dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary * tDataDic = [NSJSONSerialization JSONObjectWithData:tJsData options:NSJSONReadingMutableContainers error:nil];
    
    // 问题信息不显示 0 聊天， 1 提问
    NSNumber *type = [tDataDic objectForKey:@"type"];
    if ([type integerValue] != 0) {
        return;
    }
    
    //自己发送的收不到
    if (!peerID) {
        peerID = [TKEduSessionHandle shareInstance].localUser.peerID;
    }
    
    TKChatMessageType msgType = TKChatMessageTypeText;
    if ([[tDataDic allKeys] containsObject: @"msgtype"] &&
        [[tDataDic objectForKey:@"msgtype"] isEqualToString:@"onlyimg"]) {
        msgType = TKChatMessageTypeOnlyImage;
//        return;
    }
    
    NSString *time = [tDataDic objectForKey:@"time"];
    NSString *msg = [tDataDic objectForKey:@"msg"];
    NSString *cospath = [tDataDic objectForKey:@"cospath"];
    NSString *tMyPeerId = [TKEduSessionHandle shareInstance].localUser.peerID;
    
    BOOL isMe = [peerID isEqualToString:tMyPeerId];
    BOOL isTeacher = [extension[@"role"] intValue] == TKUserType_Teacher?YES:NO;
    TKChatRoleType roleType = (isMe)?TKChatRoleTypeMe:(isTeacher?TKChatRoleTypeTeacher:TKChatRoleTypeOtherUer);
    
    TKChatMessageModel * tChatMessageModel = [[TKChatMessageModel alloc] initWithMsgType:msgType role:roleType message:msg cospath:cospath userName:extension[@"nickname"] fromid:peerID time:time];
    [[TKEduSessionHandle shareInstance] addOrReplaceMessage:tChatMessageModel];
    
    [self reloadData];
}
#pragma mark - 刷新
- (void)reloadData
{
    _iMessageList = [[TKEduSessionHandle shareInstance] messageList];
    [self calculateCellHeight];
    [_iChatTableView reloadData];
    
    CGFloat distance = self.iChatTableView.height - self.iChatTableView.contentSize.height;
    self.iChatTableView.contentInset = UIEdgeInsetsMake(fmaxf(distance, 0), 0, 0, 0);
    
    if (_iMessageList.count>0) {
        // 滚动到消息列表最下边
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:0  inSection:_iMessageList.count - 1];
        [_iChatTableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    }
}
// 翻译
- (void)reloadDataWithIndexPath: (NSIndexPath *)indexPath {
    
    // 单独计算翻译文本
    [self calculateTranslationHeight:_iMessageList[indexPath.section]];
    // 刷新单行（组）
    [_iChatTableView reloadSections:[NSIndexSet indexSetWithIndex: indexPath.section] withRowAnimation:UITableViewRowAnimationNone];
    
    CGFloat distance = self.iChatTableView.height - self.iChatTableView.contentSize.height;
    self.iChatTableView.contentInset = UIEdgeInsetsMake(fmaxf(distance, 0), 0, 0, 0);
    
    if (_iMessageList.count > 0) {
        // 滚动到屏幕中央
        [_iChatTableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionMiddle animated:YES];
    }
}

#pragma mark - keyboard Notification
- (void)loadNotification{
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(banChat:) name:sEveryoneBanChat object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}

- (void)banChat:(NSNotification *)notification
{
    NSDictionary *message = notification.object;
    BOOL isBanSpeak = [TKUtil getBOOValueFromDic:message Key:@"isBanSpeak"];
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
        //如果是老师需要设置全体禁言按钮
        if ([TKEduSessionHandle shareInstance].isAllShutUp) {
            _rightBtn.selected = YES;
        }else{
            _rightBtn.selected = NO;
        }
        
    }else{
        
        if (isBanSpeak) {
            
            if (self.keyboardHeight != 0) [self.keyboardView.inputField resignFirstResponder];
            self.keyboardView.userInteractionEnabled = NO;
            self.keyboardView.inputField.placehoder = TKMTLocalized(@"Prompt.BanChat");
        }else{
            
            self.keyboardView.userInteractionEnabled = YES;
            self.keyboardView.inputField.placehoder = TKMTLocalized(@"Say.say");
        }
    }
    
    if ([TKEduSessionHandle shareInstance].localUser.role != TKUserType_Teacher) {
        _middleBtn.selected = isBanSpeak;
    }
}

- (void)keyboardWillShow:(NSNotification*)notification
{
    if (![self.keyboardView.inputField isFirstResponder]) {
        return;
    }
    
    // 1.键盘弹出需要的时间
    CGFloat duration = [notification.userInfo[UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    // 2.动画
    [UIView animateWithDuration:duration animations:^{
        // 取出键盘高度
        CGRect keyboardF = [notification.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
        
        
        self.keyboardHeight = keyboardF.size.height;
        
        _keyboardViewHeight = _keyboardViewHeight ? _keyboardViewHeight : chatToolHeight;
        self.keyboardView.y = ScreenH - self.keyboardHeight - _keyboardViewHeight;
        
        self.keyboardView.hidden = NO;
    }];
}

- (void)keyboardWillHide:(NSNotification *)notification
{
    self.keyboardView.inputField.text = self.keyboardView.inputField.realText;
    
    // 1.键盘弹出需要的时间
    CGFloat duration = [notification.userInfo[UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    // 2.动画
    [UIView animateWithDuration:duration animations:^{
        self.keyboardHeight = 0;
        self.keyboardView.y = ScreenH+chatToolHeight;
        self.keyboardView.hidden = YES;

    }];
    
    
}

#pragma mark - TKChatToolView Delegate
- (void)chatToolViewDidBeginEditing:(UITextView *)textView {
    
}

- (void)chatToolViewChangeHeight:(CGFloat)height {
    
    _keyboardViewHeight = height;
    self.keyboardView.y = ScreenH - self.keyboardHeight - _keyboardViewHeight;
}

- (void)sendMessage:(NSString *)message{
    
    //判断是否自己被禁言
    BOOL disablechat = [TKUtil getBOOValueFromDic:[TKEduSessionHandle shareInstance].localUser.properties Key:sDisablechat];
    if (disablechat) {
        return;
    }
    NSString *time = [TKUtil currentTime];
    NSDictionary *messageDic = @{@"type":@0,@"time":time};
    
    BOOL isSame = [[TKEduSessionHandle shareInstance] judgmentOfTheSameMessage:message lastSendTime:self.lastSendChatTime];
    
    if (isSame && _chatTimerFlag) {
        [TKUtil showMessage: TKMTLocalized(@"Prompt.NotRepeatChat")];
    }else{
        [[TKEduSessionHandle shareInstance] sessionHandleSendMessage:message toID:sTellAll extensionJson:messageDic];
        [self creatTimer];
    }
    self.chatTimerFlag = true;
    
    self.keyboardView.inputField.text = @"";
    
    [self reloadData];
    
    [self.keyboardView.inputField resignFirstResponder];
    
}
- (void)creatTimer{
    
    if (!self.chatTimer) {
        self.lastSendChatTime = [NSString stringWithFormat:@"%f", [TKUtil getNowTimeTimestamp]];
        self.chatTimer = [NSTimer scheduledTimerWithTimeInterval:3 target:self selector:@selector(timerFire) userInfo:nil repeats:YES];
    }
}
- (void)timerFire{
    
    self.chatTimerFlag = false;
    [self.chatTimer invalidate];
    self.chatTimer = nil;
    self.lastSendChatTime = nil;
}

- (void)setBadgeNumber:(float)num
{
    if (num > 0) {
        if (num < 100) {
            _badgeLabel.text = [NSString stringWithFormat:@"%d",(int)num];
        } else {
            _badgeLabel.text = @"99+";
        }
        _badgeLabel.hidden = NO;
    } else {
        
        _badgeLabel.hidden = YES;
        _badgeLabel.text = @"";
    }
}



- (UITableView *)iChatTableView {
    if (!_iChatTableView) {
        
        _iChatTableView = [[UITableView alloc]initWithFrame:CGRectZero style: UITableViewStyleGrouped];
        
        _iChatTableView.delegate   = self;
        _iChatTableView.dataSource = self;
        _iChatTableView.keyboardDismissMode = UIScrollViewKeyboardDismissModeOnDrag;
        
        _iChatTableView.backgroundColor = [UIColor clearColor];
        _iChatTableView.separatorColor  = [UIColor clearColor];
        _iChatTableView.showsHorizontalScrollIndicator = NO;
        _iChatTableView.showsVerticalScrollIndicator = NO;

        _iChatTableView.estimatedRowHeight = 0;
        _iChatTableView.estimatedSectionHeaderHeight = 0;
        _iChatTableView.estimatedSectionFooterHeight = 0;
        
        [_iChatTableView registerClass:[TKNewChatMessageTableViewCell class] forCellReuseIdentifier:NSStringFromClass([TKNewChatMessageTableViewCell class])];
        [_iChatTableView registerClass:[TKNewMessageCell class] forCellReuseIdentifier:NSStringFromClass(TKNewMessageCell.class)];
        [_iChatTableView registerClass:[TKNewPictureCell class] forCellReuseIdentifier:NSStringFromClass(TKNewPictureCell.class)];
    }
    
    return _iChatTableView;
}

- (TKChatToolView *)keyboardView {
    if (!_keyboardView) {
        BOOL isIphoneX = NO;
        if ([TKUtil IS_IPHONEX] || IS_IPHONE_X) {
            isIphoneX = YES;
        }
        
        CGFloat x = isIphoneX ?  44 : 0;
        
        _keyboardView = [[TKChatToolView alloc] initWithFrame:CGRectMake(x,
                                                                             ScreenH + chatToolHeight,
                                                                             ScreenW - x,
                                                                             chatToolHeight) isDistance:true];
        _keyboardView.sakura.backgroundColor(ThemeKP(@"chatToolBackColor"));
        _keyboardView.delegate = self;
        [TKMainWindow addSubview:_keyboardView];
    }
    return _keyboardView;
}

//渐变_gLayer随动
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    //直接修改属性会产生隐式动画，时长为0.25s，这样导致滑动时不跟手，可以用上面的CABasicAnimation设置动画时长为0.
    //或者使用下面的CATransaction动画事务，setDisableActions:YES或者setAnimationDuration:0都可以达到效果，
    //但是setDisableActions应该性能更高吧，毕竟是直接关闭了动画。setAnimationDuration仍然是设置动画时长为0
    
    [CATransaction begin];
    [CATransaction setDisableActions:YES];
    _gLayer.position = CGPointMake(0, scrollView.contentOffset.y);
    [CATransaction commit];
}

//判断只有point落在本view的button或者table上才有效，避免本透明view遮挡白板操作
- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event
{
    //    NSArray<UIView *> *arr = @[_leftBtn, _middleBtn, _rightBtn, self.iChatTableView];
    NSMutableArray<UIView *> *mArr = [@[] mutableCopy];
    [mArr addObject:_leftBtn];
    
    if (!_middleBtn.hidden) {
        [mArr addObject:_middleBtn];
    }
    
    if (!_rightBtn.hidden) {
        [mArr addObject:_rightBtn];
    }
    
    if (self.iChatTableView.alpha != 0) {
        [mArr addObject:self.iChatTableView];
    }
    
    BOOL pointOnSelf = NO;
    
    for (UIView *tmp in mArr) {
        CGPoint viewPoint = [self convertPoint:point toView:tmp];
        if ([tmp isKindOfClass:UITableView.class]) {
            //只有当point在可显示的cell的bubble上时才可触发点击，避免遮挡后面的按钮
            for (UITableViewCell *cell in self.iChatTableView.visibleCells) {
                if ([cell isKindOfClass:[TKNewMessageCell class]]) {
                    TKNewMessageCell *mCell = (TKNewMessageCell *)cell;
                    CGPoint pointOnCell = [self.iChatTableView convertPoint:viewPoint toView:mCell];
                    if (pointOnCell.x >= 0 && pointOnCell.x <= mCell.bubbleView.width && pointOnCell.y >= 0 && pointOnCell.y <= mCell.height) {
                        pointOnSelf = YES;
                    }
                } else {
                    TKNewChatMessageTableViewCell *cCell = (TKNewChatMessageTableViewCell *)cell;
                    CGPoint pointOnCell = [self.iChatTableView convertPoint:viewPoint toView:cCell];
                    if (pointOnCell.x >= 0 && pointOnCell.x <= cCell.bubbleView.width && pointOnCell.y >= 0 && pointOnCell.y <= cCell.height) {
                        pointOnSelf = YES;
                    }
                }
            }
            
        } else {
            if (viewPoint.x >= 0 && viewPoint.x <= tmp.width && viewPoint.y >= 0 && viewPoint.y <= tmp.height) {
                pointOnSelf = YES;
            }
        }
    }
    
    return pointOnSelf;
}

- (void)showMessageView:(UIButton *)sender
{
    [self loadNotification];
    //只有老师可以显示禁言按钮
    _rightBtn.hidden = !([TKEduSessionHandle shareInstance].roomMgr.localUser.role == TKUserType_Teacher);
    //巡课隐藏发言按钮
    _middleBtn.hidden = ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol);
    
    sender.selected = !sender.selected;
    if (self.messageBtnClickBlock) {
        self.messageBtnClickBlock(sender);
    }
    [UIView animateWithDuration:0.3f animations:^{
        if (sender.selected) {
            _middleBtn.x = _btnWidth + _btnSpace;
            _rightBtn.x = (_btnWidth + _btnSpace) * (_middleBtn.hidden ? 1 : 2);
            _middleBtn.alpha = _rightBtn.alpha = 1;
        } else {
            _middleBtn.alpha = _rightBtn.alpha = _middleBtn.x = _rightBtn.x = 0;
        }
        
        self.iChatTableView.alpha = sender.selected ? 1 : 0;
    } completion:^(BOOL finished) {
        if (sender.selected) {
            [self reloadData];
        }
    }];
}

- (void)setUserRoleType:(TKUserRoleType)type
{
    //TODO :暂时只有学生隐藏禁言按钮
    switch (type) {
        case TKUserType_Teacher:
        {
            //老师
            _rightBtn.hidden = _middleBtn.hidden = NO;
            break;
        }
        case TKUserType_Student:
        {
            //学生
            _rightBtn.hidden = YES;
            break;
        }
        case TKUserType_Patrol:
        {
            //巡课
            _rightBtn.hidden = _middleBtn.hidden = YES;
            break;
        }
        case TKUserType_Playback:
        {
            //回放
            break;
        }
        case TKUserType_Live:
        {
            //直播
            break;
        }
        case TKUserType_Assistant:
        {
            //助教
            break;
        }
        default:
            break;
    }
}

- (void)input
{
    if (_middleBtn.selected) {
        //不能发消息 提示： 你已经被禁言，无法发送消息
        [TKUtil showMessage:@"你已经被禁言，无法发送消息"];

    }else{
        // 从window移除后 导致键盘无法显示，需要重新add
        if (!self.keyboardView.superview) {
            CGFloat x = IS_IPHONE_X ?  44 : 0;
            self.keyboardView.frame = CGRectMake(x, ScreenH+44, ScreenW - x, 44);
            [TKMainWindow addSubview:self.keyboardView];
        }
        self.keyboardView.emotionButton.selected = self.keyboardView.isCustomInputView;
        self.keyboardView.hidden = NO;
        [TKMainWindow bringSubviewToFront:self.keyboardView];
        [self.keyboardView.inputField becomeFirstResponder];
    }
}

- (void)hide:(BOOL)hide
{
    if (hide) {
        //不知道为什么此处隐藏chatView 需要移除监听，
        //fix bug TALK-6797 当移除监听前，回收键盘，防止键盘不回收的情况
        [self.keyboardView.inputField resignFirstResponder];
        //退出教室后 屏幕从横屏转向竖屏 键盘会显示出来
        self.keyboardView.hidden = YES;
        
        [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillShowNotification object:nil];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:UIKeyboardWillHideNotification object:nil];
    } else {
        [self loadNotification];
        self.keyboardView.hidden = NO;
    }
    
    [UIView animateWithDuration:0.3f animations:^{
        if (!hide) {
            _middleBtn.x = _btnWidth + _btnSpace;
            _rightBtn.x = (_btnWidth + _btnSpace) * 2;
            _middleBtn.alpha = _rightBtn.alpha = 1;
        } else {
            _middleBtn.alpha = _rightBtn.alpha = _middleBtn.x = _rightBtn.x = 0;
        }
        self.iChatTableView.alpha = !hide ? 1 : 0;
    } completion:^(BOOL finished) {
        if (!hide) {
            [self reloadData];
        }
        _leftBtn.selected = !hide;
    }];
    
    if (_hideComplete) {
        _hideComplete();
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    TKChatMessageModel *tMessageModel = [_iMessageList objectAtIndex:indexPath.section];
    
    switch (tMessageModel.iChatMessageType) {
        case TKChatMessageTypeText:
        {
            TKNewChatMessageTableViewCell * tCell =[tableView dequeueReusableCellWithIdentifier:NSStringFromClass(TKNewChatMessageTableViewCell.class) forIndexPath:indexPath];
            tCell.chatModel = tMessageModel;
            return tCell;
            break;
        }
        case TKChatMessageTypeOnlyImage:
        {
            TKNewPictureCell * tCell = [tableView dequeueReusableCellWithIdentifier:NSStringFromClass([TKNewPictureCell class]) forIndexPath:indexPath];
            tCell.chatModel = tMessageModel;
            return tCell;
            break;
        }
        case TKChatMessageTypeTips:
        {
            
            TKNewMessageCell *tCell = [tableView dequeueReusableCellWithIdentifier:NSStringFromClass(TKNewMessageCell.class) forIndexPath:indexPath];
            [tCell setSelectionStyle:UITableViewCellSelectionStyleNone];
            tCell.iMessageText = [NSString stringWithFormat:@"%@", tMessageModel.iMessage];
            [tCell setTextColor:tMessageModel.iMessageTypeColor];
            return tCell;
            break;
        }
        default:
        {
            UITableViewCell * cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"cell"];
            [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
            return cell;
        }
    }
}

#pragma mark - 计算缓存 cell 高度
- (void)calculateCellHeight {
    
    [self.iMessageList enumerateObjectsWithOptions:NSEnumerationReverse usingBlock:^(TKChatMessageModel * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        
        // 系统消息不计算
        if (obj.iChatMessageType == TKChatMessageTypeTips) {
            
            obj.height = 30. + 8;
        } else if (obj.iChatMessageType == TKChatMessageTypeOnlyImage) {
            
            obj.height = 100;
        } else {
            
            // 文字高度
            if (!(obj.messageHeight > 0)) {// 如果未计算过
                float nameWidth = [TKHelperUtil sizeForString:[NSString stringWithFormat:@"%@:", (obj.iChatRoleType == TKChatRoleTypeMe) ? TKMTLocalized(@"Role.Me") : obj.iUserName] font:TKFont(14) size:CGSizeMake(fminf(self.iChatTableView.width / 3, 100), CGFLOAT_MAX)].width;
                float limitWidth = self.iChatTableView.width - 11 - nameWidth - 10 - 10 - 10 - 22;
                
                CGFloat msgHeight = [TKNewChatMessageTableViewCell heightForCellWithText:obj.iMessage.length > 0 ? obj.iMessage : @" "
                                                                              limitWidth:limitWidth];
                
                obj.messageHeight = msgHeight;
                obj.height = 16 * Proportion +// 消息内容上边距
                obj.messageHeight + 16 * Proportion + 8;  // 时间高度
            }
            // 翻译高度
            [self calculateTranslationHeight:obj];
        }

    }];
}

// 计算翻译高度
- (void)calculateTranslationHeight:(TKChatMessageModel *)obj {
    
    // 翻译高度
    if (obj.iTranslationMessage.length > 0 && !(obj.translationHeight > 0)) {// 如果未计算过
        float nameWidth = [TKHelperUtil sizeForString:[NSString stringWithFormat:@"%@:", (obj.iChatRoleType == TKChatRoleTypeMe) ? TKMTLocalized(@"Role.Me") : obj.iUserName] font:TKFont(14) size:CGSizeMake(fminf(self.iChatTableView.width / 3, 100), CGFLOAT_MAX)].width;
        float msgWidth = self.iChatTableView.width - 11 - nameWidth - 10 - 10 - 10 - 11;
        CGSize msgSize = [TKHelperUtil sizeForString:obj.iMessage.length > 0 ? obj.iMessage : @" " font:TKFont(14) size:CGSizeMake(msgWidth, CGFLOAT_MAX)];
        float cellWidth = 11 + nameWidth + 10 + msgSize.width + 10 + 11 + 10;
        
        obj.translationHeight = [TKHelperUtil sizeForString:obj.iTranslationMessage font:TKFont(14) size:CGSizeMake(cellWidth - 20, CGFLOAT_MAX)].height;
        obj.height += 10 * 2 + // 分割线上下边距
        obj.translationHeight + 0;
    }
}

#pragma mark - tableViewDelegate
-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.iMessageList.count;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return self.iMessageList[indexPath.section].height;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 0.01f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.01;
}

#pragma mark - 翻译
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (_iMessageList.count <= indexPath.section) {
        return;
    }
    
    TKChatMessageModel *tMessageModel = [_iMessageList objectAtIndex:indexPath.section];
    
    if(tMessageModel.iTranslationMessage.length > 0){
        return;
    }
    // 内容为消息 直接返回
    if (tMessageModel.iChatMessageType == TKChatMessageTypeTips ||
        tMessageModel.iChatMessageType == TKChatMessageTypeOnlyImage) {
        return;
    }
    
    __weak __typeof(self)weakSelf = self;
    [TKEduNetManager translation:tMessageModel.iMessage aTranslationComplete:^int(id  _Nullable response, NSString * _Nullable aTranslationString) {
        __strong __typeof(weakSelf) strongSelf = weakSelf;
        tMessageModel.iTranslationMessage = aTranslationString;
        [[TKEduSessionHandle shareInstance] addTranslationMessage:tMessageModel];
        [strongSelf reloadDataWithIndexPath: indexPath];
        
        return 0;
    }];
    
}

- (void)removeSubviews
{
    [self.keyboardView removeFromSuperview];
}

- (void)dealloc{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end
