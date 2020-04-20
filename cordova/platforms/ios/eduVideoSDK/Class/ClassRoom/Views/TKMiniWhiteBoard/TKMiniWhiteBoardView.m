//
//  TKMiniWhiteBoardView.m
//  TKWhiteBoard
//
//  Created by 周洁 on 2019/1/7.
//  Copyright © 2019 MAC-MiNi. All rights reserved.
//

#import "TKMiniWhiteBoardView.h"
#import "Masonry.h"
#import <TKRoomSDK/TKRoomSDK.h>

#define ThemeKP(args) [@"TKNativeWB.LightWB." stringByAppendingString:args]

@implementation TKMiniWhiteBoardView
{
    UIButton *_closeBtn;
    UIView *_drawToolView;
    
    UIButton *_penBtn;
    UIButton *_textBtn;
    UIButton *_eraserBtn;
    
    UIButton *_sendBtn;
    UIPanGestureRecognizer *_panG;
    
    NSMutableArray <NSDictionary *> *_prepareData;

}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (instancetype)init
{
    if (self = [super init]) {
        _prepareData = [@[] mutableCopy];
        
        self.sakura.backgroundColor(ThemeKP(@"bg_color"));
        self.layer.masksToBounds = YES;
        self.layer.cornerRadius = 10 * Proportion;
        
        _closeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _closeBtn.sakura.backgroundImage(ThemeKP(@"tk_close"), UIControlStateNormal);
        [self addSubview:_closeBtn];
        [_closeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.mas_right).offset(-10 * Proportion);
            make.top.equalTo(self.mas_top).offset(10 * Proportion);
            make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(20 * Proportion, 20 * Proportion)]);
        }];
        
        [_closeBtn addTarget:self action:@selector(closeMiniWhiteBoard) forControlEvents:UIControlEventTouchUpInside];
        
        _segmentCotnrol = [[TKStudentSegmentControl alloc] initWithDelegate:self];
        [self addSubview:_segmentCotnrol];
        [_segmentCotnrol mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left).offset(5 * Proportion);
            make.top.equalTo(self.mas_top).offset(5 * Proportion);
            make.height.equalTo(@((37 + 10) * Proportion));
            make.right.equalTo(self.mas_right).offset(-96 * Proportion);
        }];
        
        UIView *underDrawView = [[UIView alloc] init];
        underDrawView.sakura.backgroundColor(ThemeKP(@"tip_bg_sel_color"));
        [self addSubview:underDrawView];
        [underDrawView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left).offset(5 * Proportion);
            make.right.equalTo(self.mas_right).offset(-5 * Proportion);
            make.top.equalTo(self.mas_top).offset(42 * Proportion);
            make.bottom.equalTo(self.mas_bottom).offset(-53 * Proportion);
            make.width.equalTo(underDrawView.mas_height).multipliedBy(16 / 9.0f).priorityHigh();
        }];
        
        _tkDrawView = [[TKDrawView alloc] initWithDelegate:self];
        [_tkDrawView setWorkMode:TKWorkModeControllor];
        [_tkDrawView switchToFileID:sBlackBoardCommon pageID:1 refreshImmediately:YES];
        [self addSubview:_tkDrawView];
        [_tkDrawView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(underDrawView.mas_left);
            make.right.equalTo(underDrawView.mas_right);
            make.top.equalTo(underDrawView.mas_top);
            make.bottom.equalTo(underDrawView.mas_bottom);
        }];
        
        TKStudentSegmentObject *teacher = [[TKStudentSegmentObject alloc] init];
        teacher.ID = sBlackBoardCommon;
        teacher.currentPage = 1;
        teacher.seq = @(0);
        _choosedStudent = teacher;
        
        _drawToolView = [[UIView alloc] init];
        [self addSubview:_drawToolView];
        
        _penBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _penBtn.sakura.backgroundImage(ThemeKP(@"tk_pen_swb_default"), UIControlStateNormal);
        _penBtn.sakura.backgroundImage(ThemeKP(@"tk_pen_swb_selected"), UIControlStateSelected);
        [_penBtn addTarget:self action:@selector(drawPen:) forControlEvents:UIControlEventTouchUpInside];
        _penBtn.selected = YES;
        [_drawToolView addSubview:_penBtn];
        [_penBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(_drawToolView.mas_left);
            make.centerY.equalTo(_drawToolView.mas_centerY);
            make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(39 * Proportion, 25 * Proportion)]);
        }];
        
        _textBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _textBtn.sakura.backgroundImage(ThemeKP(@"tk_text_swb_default"), UIControlStateNormal);
        _textBtn.sakura.backgroundImage(ThemeKP(@"tk_text_swb_selected"), UIControlStateSelected);
        [_textBtn addTarget:self action:@selector(drawText:) forControlEvents:UIControlEventTouchUpInside];
        [_drawToolView addSubview:_textBtn];
        [_textBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(_penBtn.mas_right).offset(40 * Proportion);
            make.centerY.equalTo(_drawToolView.mas_centerY);
            make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(39 * Proportion, 25 * Proportion)]);
        }];
        
        _eraserBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _eraserBtn.sakura.backgroundImage(ThemeKP(@"tk_xiangpi_swb_default"), UIControlStateNormal);
        _eraserBtn.sakura.backgroundImage(ThemeKP(@"tk_xiangpi_swb_selected"), UIControlStateSelected);
        [_eraserBtn addTarget:self action:@selector(drawEraser:) forControlEvents:UIControlEventTouchUpInside];
        [_drawToolView addSubview:_eraserBtn];
        [_eraserBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(_textBtn.mas_right).offset(40 * Proportion);
            make.centerY.equalTo(_drawToolView.mas_centerY);
            make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(39 * Proportion, 25 * Proportion)]);
        }];
        
        [_drawToolView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(self.mas_left).offset(31 * Proportion);
            make.right.equalTo(_eraserBtn.mas_right);
            make.top.equalTo(_tkDrawView.mas_bottom);
            make.bottom.equalTo(self.mas_bottom);
        }];
        
        _selectorView = [[TKBrushSelectorView alloc] initWithDefaultColor:nil];
        _selectorView.clipsToBounds = YES;
        _selectorView.delegate = self;
        
        _sendBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _sendBtn.sakura.backgroundImage(ThemeKP(@"tk_button_send_default"), UIControlStateNormal);
        [_sendBtn addTarget:self action:@selector(sendState) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_sendBtn];
        [_sendBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self.mas_right).offset(-12 * Proportion);
            make.bottom.equalTo(self.mas_bottom).offset(-4 * Proportion);
            make.size.equalTo([NSValue valueWithCGSize:CGSizeMake(115 * Proportion, 44 * Proportion)]);
        }];
        
        _panG = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(panGesture:)];
        [self addGestureRecognizer:_panG];
        
        [self setNeedsLayout];
        [self layoutIfNeeded];
    }
    
    return self;
}

- (void)panGesture:(UIPanGestureRecognizer *)panG
{
    CGPoint translatedPoint = [panG translationInView:self];
    CGFloat x = self.center.x + translatedPoint.x;
    CGFloat y = self.center.y + translatedPoint.y;
    if (panG.state == UIGestureRecognizerStateBegan) {
        if (CGRectContainsPoint(_tkDrawView.frame, [panG locationInView:self])) {
            panG.enabled = NO;
        }
    } else if (panG.state == UIGestureRecognizerStateChanged) {

        CGPoint deltaCenter = CGPointMake(x - self.superview.frame.size.width / 2, y - self.superview.frame.size.height / 2);
        
        [self mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.left.greaterThanOrEqualTo(self.superview.mas_left).priorityHigh();
            make.right.lessThanOrEqualTo(self.superview.mas_right).priorityHigh();
            make.bottom.greaterThanOrEqualTo(self.superview.mas_bottom).priorityHigh();
            make.top.lessThanOrEqualTo(self.superview.mas_top).priorityHigh();
            make.centerX.equalTo(self.superview.mas_centerX).offset(deltaCenter.x).priorityLow();
            make.centerY.equalTo(self.superview.mas_centerY).offset(deltaCenter.y).priorityLow();
            make.width.equalTo(@(self.frame.size.width));
            make.height.equalTo(@(self.frame.size.height));
        }];
    }
    
    [panG setTranslation:CGPointMake(0, 0) inView:self];
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    //设定白板相对web比例
    _tkDrawView.iFontScale = _tkDrawView.frame.size.height / 960;
}

- (void)setDefaultDrawData
{
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
        [_tkDrawView setDrawType:TKDrawTypePen hexColor:@"#ED3E3A" progress:0.05f];
    }
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
        [_tkDrawView setDrawType:TKDrawTypePen hexColor:@"#160C30" progress:0.05f];
    }
}

- (void)sendStudent
{
    if (self.isBigRoom) {
        //大并发教室自己未上台则不发自己
        if ([TKEduSessionHandle shareInstance].localUser.publishState == 0) {
            return;
        }
    }
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
        __block BOOL has = NO;
        [_segmentCotnrol.students enumerateObjectsUsingBlock:^(TKStudentSegmentObject * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            if ([obj.ID isEqualToString:[TKEduSessionHandle shareInstance].localUser.peerID]) {
                has = YES;
            }
        }];
        if (!has) {
            
            NSDictionary * pubDict = @{@"id" : [TKEduSessionHandle shareInstance].localUser.peerID,
                                       @"nickname" : [TKEduSessionHandle shareInstance].localUser.nickName, @"role" : @(2)};
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sUserHasNewBlackBoard
                                                                 ID:[NSString stringWithFormat:@"_%@",[TKEduSessionHandle shareInstance].localUser.peerID]
                                                                 To:sTellAll
                                                               Data:pubDict
                                                               Save:YES
                                                    AssociatedMsgID:sBlackBoard_new
                                                   AssociatedUserID:[TKEduSessionHandle shareInstance].localUser.peerID
                                                            expires:0
                                                         completion:nil];
        }
    }
}

//接收状态
- (void)switchStates:(TKMiniWhiteBoardState)state
{
    if (![_tkDrawView hasDraw]) {
        [self setDefaultDrawData];
    }
    self.state = state;
    switch (state) {
        case TKMiniWhiteBoardStatePrepareing:
        {
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                _segmentCotnrol.hidden = YES;
                _closeBtn.hidden = YES;
                _sendBtn.hidden = YES;
                _drawToolView.hidden = NO;
                _segmentCotnrol.userInteractionEnabled = NO;
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
                _segmentCotnrol.hidden = YES;
                _closeBtn.hidden = NO;
                _sendBtn.hidden = NO;
                _drawToolView.hidden = NO;
                _segmentCotnrol.userInteractionEnabled = YES;
                
                NSDictionary * pubDict = @{@"id" : sBlackBoardCommon, @"nickname" : [TKEduSessionHandle shareInstance].localUser.nickName, @"role" : @(0)};
                [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sUserHasNewBlackBoard
                                                                     ID:[NSString stringWithFormat:@"_%@",[TKEduSessionHandle shareInstance].localUser.peerID]
                                                                     To:sTellAll
                                                                   Data:pubDict
                                                                   Save:YES
                                                        AssociatedMsgID:sBlackBoard_new
                                                       AssociatedUserID:sBlackBoardCommon
                                                                expires:0
                                                             completion:nil];
            }
            
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
                _segmentCotnrol.hidden = YES;
                _closeBtn.hidden = YES;
                _sendBtn.hidden = YES;
                _drawToolView.hidden = YES;
                _segmentCotnrol.userInteractionEnabled = _drawToolView.userInteractionEnabled = _sendBtn.userInteractionEnabled = _closeBtn.userInteractionEnabled = _segmentCotnrol.userInteractionEnabled = NO;
            }
            
            [_sendBtn setAttributedTitle:[[NSAttributedString alloc] initWithString:TKMTLocalized(@"MiniWB.Dispense") attributes:@{NSForegroundColorAttributeName : UIColor.whiteColor, NSFontAttributeName : [UIFont systemFontOfSize:16 * Proportion]}] forState:UIControlStateNormal];

            break;
        }
        case TKMiniWhiteBoardStateDispenseed:
        {
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                 _segmentCotnrol.hidden = YES;
                _drawToolView.hidden = NO;
                _sendBtn.hidden = YES;
                _closeBtn.hidden = YES;
                _segmentCotnrol.userInteractionEnabled = NO;
                [self sendStudent];
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
                _segmentCotnrol.hidden = NO;
                _drawToolView.hidden = NO;
                _sendBtn.hidden = NO;
                _closeBtn.hidden = NO;
                _segmentCotnrol.userInteractionEnabled = YES;
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
                _segmentCotnrol.hidden = NO;
                _drawToolView.hidden = YES;
                _sendBtn.hidden = YES;
                _closeBtn.hidden = YES;
                _segmentCotnrol.userInteractionEnabled = _drawToolView.userInteractionEnabled = _sendBtn.userInteractionEnabled = _closeBtn.userInteractionEnabled = _segmentCotnrol.userInteractionEnabled = NO;
            }
            
            [_sendBtn setAttributedTitle:[[NSAttributedString alloc] initWithString:TKMTLocalized(@"MiniWB.Recycle") attributes:@{NSForegroundColorAttributeName : UIColor.whiteColor, NSFontAttributeName : [UIFont systemFontOfSize:16 * Proportion]}] forState:UIControlStateNormal];
            
            break;
        }
        case TKMiniWhiteBoardStateAgainDispenseed:
        {
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                _segmentCotnrol.hidden = YES;
                _drawToolView.hidden = NO;
                _sendBtn.hidden = YES;
                _closeBtn.hidden = YES;
                _segmentCotnrol.userInteractionEnabled = NO;
                [self sendStudent];
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
                _segmentCotnrol.hidden = NO;
                _drawToolView.hidden = NO;
                _sendBtn.hidden = NO;
                _closeBtn.hidden = NO;
                _segmentCotnrol.userInteractionEnabled = YES;
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
                _segmentCotnrol.hidden = NO;
                _drawToolView.hidden = YES;
                _sendBtn.hidden = YES;
                _closeBtn.hidden = YES;
                _segmentCotnrol.userInteractionEnabled = _drawToolView.userInteractionEnabled = _sendBtn.userInteractionEnabled = _closeBtn.userInteractionEnabled = _segmentCotnrol.userInteractionEnabled = NO;
            }
            
            [_sendBtn setAttributedTitle:[[NSAttributedString alloc] initWithString:TKMTLocalized(@"MiniWB.Recycle") attributes:@{NSForegroundColorAttributeName : UIColor.whiteColor, NSFontAttributeName : [UIFont systemFontOfSize:16 * Proportion]}] forState:UIControlStateNormal];
            break;
        }
        case TKMiniWhiteBoardStateRecycle:
        {
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                _segmentCotnrol.hidden = NO;
                _drawToolView.hidden = YES;
                _sendBtn.hidden = YES;
                _closeBtn.hidden = YES;
                _segmentCotnrol.userInteractionEnabled = NO;
                [self sendStudent];
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
                _segmentCotnrol.hidden = NO;
                _drawToolView.hidden = NO;
                _sendBtn.hidden = NO;
                _closeBtn.hidden = NO;
                _segmentCotnrol.userInteractionEnabled = YES;
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
                _segmentCotnrol.hidden = NO;
                _drawToolView.hidden = YES;
                _sendBtn.hidden = YES;
                _closeBtn.hidden = YES;
                _segmentCotnrol.userInteractionEnabled = YES;
                _segmentCotnrol.userInteractionEnabled = _drawToolView.userInteractionEnabled = _sendBtn.userInteractionEnabled = _closeBtn.userInteractionEnabled = _segmentCotnrol.userInteractionEnabled = NO;
            }

            [_sendBtn setAttributedTitle:[[NSAttributedString alloc] initWithString:TKMTLocalized(@"MiniWB.Redispense") attributes:@{NSForegroundColorAttributeName : UIColor.whiteColor, NSFontAttributeName : [UIFont systemFontOfSize:16 * Proportion]}] forState:UIControlStateNormal];
            break;
        }
        
        default:
            break;
    }
}

//老师点击按钮发送状态
- (void)sendState
{
    switch (self.state) {
        case TKMiniWhiteBoardStateDispenseed:
        {
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sBlackBoard_new ID:sBlackBoard_new To:sTellAll Data:@{@"blackBoardState" : @"_recycle", @"currentTapKey" : sBlackBoardCommon, @"currentTapPage" : @(1)} Save:YES AssociatedMsgID:sClassBegin AssociatedUserID:nil expires:0 completion:nil];
            break;
        }
        case TKMiniWhiteBoardStateAgainDispenseed:
        {
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sBlackBoard_new ID:sBlackBoard_new To:sTellAll Data:@{@"blackBoardState" : @"_recycle", @"currentTapKey" : sBlackBoardCommon, @"currentTapPage" : @(1)} Save:YES AssociatedMsgID:sClassBegin AssociatedUserID:nil expires:0 completion:nil];
            break;
        }
        case TKMiniWhiteBoardStateRecycle:
        {
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sBlackBoard_new ID:sBlackBoard_new To:sTellAll Data:@{@"blackBoardState" : @"_againDispenseed", @"currentTapKey" : sBlackBoardCommon, @"currentTapPage" : @(1)} Save:YES AssociatedMsgID:sClassBegin AssociatedUserID:nil expires:0 completion:nil];
            break;
        }
        case TKMiniWhiteBoardStatePrepareing:
        {
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sBlackBoard_new ID:sBlackBoard_new To:sTellAll Data:@{@"blackBoardState" : @"_dispenseed", @"currentTapKey" : sBlackBoardCommon, @"currentTapPage" : @(1)} Save:YES AssociatedMsgID:sClassBegin AssociatedUserID:nil expires:0 completion:nil];
            break;
        }
            
        default:
            break;
    }
}

- (void)closeMiniWhiteBoard
{
    self.hidden = YES;
    [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sBlackBoard_new ID:sBlackBoard_new To:sTellAll Data:@{} completion:nil];
}

//增加学生画布
- (BOOL)addStudent:(TKStudentSegmentObject *)student
{
    return [_segmentCotnrol addStudent:student];
}

//移除学生画布
- (void)removeStudent:(TKStudentSegmentObject *)student
{
    [_segmentCotnrol removeStudent:student];
    [_tkDrawView clearOnePageWithFileID:student.ID pageNum:1];
}

//选中学生
- (void)didSelectStudent:(TKStudentSegmentObject *)student
{
    _choosedStudent = student;
    switch (self.state) {
        case TKMiniWhiteBoardStateDispenseed:
        {
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sBlackBoard_new ID:sBlackBoard_new To:sTellAll Data:@{@"blackBoardState" : @"_dispenseed", @"currentTapKey" : student.ID, @"currentTapPage" : @(1)} Save:YES AssociatedMsgID:sClassBegin AssociatedUserID:nil expires:0 completion:nil];
            break;
        }
        case TKMiniWhiteBoardStateRecycle:
        {
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sBlackBoard_new ID:sBlackBoard_new To:sTellAll Data:@{@"blackBoardState" : @"_recycle", @"currentTapKey" : student.ID, @"currentTapPage" : @(1)} Save:YES AssociatedMsgID:sClassBegin AssociatedUserID:nil expires:0 completion:nil];
            break;
        }
        case TKMiniWhiteBoardStateAgainDispenseed:
        {
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sBlackBoard_new ID:sBlackBoard_new To:sTellAll Data:@{@"blackBoardState" : @"_againDispenseed", @"currentTapKey" : student.ID, @"currentTapPage" : @(1)} Save:YES AssociatedMsgID:sClassBegin AssociatedUserID:nil expires:0 completion:nil];
            break;
        }
            
        default:
            break;
    }
}

- (void)chooseStudent:(TKStudentSegmentObject *)student
{
    _choosedStudent = student;
    [_segmentCotnrol chooseStudent:student];
    //老师在分发状态下只能在自己画布上绘制，回收状态可以在所有画布上绘制
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
        if (self.state == TKMiniWhiteBoardStateAgainDispenseed || self.state == TKMiniWhiteBoardStateDispenseed) {
            if ([student.ID isEqualToString:sBlackBoardCommon]) {
                [_tkDrawView setWorkMode:TKWorkModeControllor];
            } else {
                [_tkDrawView setWorkMode:TKWorkModeViewer];
            }
        } else {
            [_tkDrawView setWorkMode:TKWorkModeControllor];
        }
    }
}

- (void)drawPen:(UIButton *)btn
{
    _penBtn.selected = YES;
    _textBtn.selected = NO;
    _eraserBtn.selected = NO;
    
    [_selectorView showType:TKSelectorShowTypeMiddle];
    [_selectorView showOnMiniWhiteBoardAboveView:btn type:TKBrushToolTypeLine];
}

- (void)drawText:(UIButton *)btn
{
    _penBtn.selected = NO;
    _textBtn.selected = YES;
    _eraserBtn.selected = NO;
    
    [_selectorView showType:TKSelectorShowTypeMiddle];
    [_selectorView showOnMiniWhiteBoardAboveView:btn type:TKBrushToolTypeText];
}

- (void)drawEraser:(UIButton *)btn
{
    _penBtn.selected = NO;
    _textBtn.selected = NO;
    _eraserBtn.selected = YES;
    
    [_selectorView showType:TKSelectorShowTypeLow];
    [_selectorView showOnMiniWhiteBoardAboveView:btn type:TKBrushToolTypeEraser];
}

//选择画笔工具回调数据
- (void)brushSelectorViewDidSelectDrawType:(TKDrawType)type color:(NSString *)hexColor widthProgress:(float)progress
{
    [_tkDrawView setDrawType:type hexColor:hexColor progress:progress];
}

//发送小白板绘制数据
- (void)addSharpWithFileID:(NSString *)fileid shapeID:(NSString *)shapeID shapeData:(NSData *)shapeData
{
    [_selectorView removeFromSuperview];
    
    /******************************************************************************************************/
    //老师：回收状态下可以在任意画布上绘制，分发状态下只能在自己画布上绘制
    //学生：回收状态无法绘制，分发状态下可以在自己画布上绘制
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
        if (self.state == TKMiniWhiteBoardStateDispenseed || self.state == TKMiniWhiteBoardStateAgainDispenseed) {
            if (![fileid isEqualToString:sBlackBoardCommon]) {
                return;
            }
        }
    }
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
        if (self.state == TKMiniWhiteBoardStateDispenseed || self.state == TKMiniWhiteBoardStateAgainDispenseed) {
            if (![fileid isEqualToString:[TKEduSessionHandle shareInstance].localUser.peerID]) {
                return;
            }
        }
        if (self.state == TKMiniWhiteBoardStateRecycle) {
            return;
        }
    }
    /******************************************************************************************************/
    
    NSMutableDictionary *dic = [NSJSONSerialization JSONObjectWithData:shapeData options:NSJSONReadingMutableContainers error:nil];

    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
        [dic setObject:_tkDrawView.fileid forKey:@"whiteboardID"];
        switch (self.state) {
            case TKMiniWhiteBoardStateDispenseed:

            case TKMiniWhiteBoardStateRecycle:

            case TKMiniWhiteBoardStateAgainDispenseed:
            {
                [dic setObject:@(NO) forKey:@"isBaseboard"];
                break;
            }
            case TKMiniWhiteBoardStatePrepareing:
            {
                [dic setObject:@(YES) forKey:@"isBaseboard"];
                break;
            }
            default:
                break;
        }
    }
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
        [dic setObject:[TKEduSessionHandle shareInstance].localUser.peerID forKey:@"whiteboardID"];
        [dic setObject:@(NO) forKey:@"isBaseboard"];
    }
    
    [dic setObject:[TKEduSessionHandle shareInstance].localUser.nickName forKey:@"nickname"];

    NSData *newData = [NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:nil];
    NSString *data = [[NSString alloc] initWithData:newData encoding:NSUTF8StringEncoding];
    NSString *s1 = [data stringByReplacingOccurrencesOfString:@"\n" withString:@""];

    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sSharpsChange ID:shapeID To:sTellAll Data:s1 Save:YES AssociatedMsgID:sBlackBoard_new AssociatedUserID:nil expires:0 completion:nil];
    
    _panG.enabled = YES;
}

//每次隐藏后清理数据
- (void)clear
{
    _choosedStudent = nil;
    [_tkDrawView clearDataAfterClass];
    [_tkDrawView setNeedsDisplay];
    [_segmentCotnrol resetUI];
}

//画笔工具超出了self，保证超出部分也可点击
- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event
{
    //移除画笔选择工具
    if ([_selectorView pointInside:[self convertPoint:point toView:_selectorView] withEvent:event] == NO) {

        [_selectorView removeFromSuperview];
    }
        
    //触摸到就放置最前
    [self.superview bringSubviewToFront:self];
    
    _panG.enabled = YES;
    if (CGRectContainsPoint(self.bounds, point)) {
        return YES;
    }
    
    if (CGRectContainsPoint(_selectorView.frame, point)) {
        _panG.enabled = NO;
        return YES;
    }
    
    return NO;
}

- (void)handleSignal:(NSDictionary *)dictionary isDel:(BOOL)isDel
{
    if (!dictionary || dictionary.count == 0) {
        return;
    }
    
    //信令相关性
    NSString *associatedMsgID = [dictionary objectForKey:sAssociatedMsgID];
    
    //信令名
    NSString *msgName = [dictionary objectForKey:sName];
    
    //信令内容
    id dataObject = [dictionary objectForKey:@"data"];
    NSMutableDictionary *data = nil;
    if ([dataObject isKindOfClass:[NSDictionary class]]) {
        data = [NSMutableDictionary dictionaryWithDictionary:dataObject];
    }
    if ([dataObject isKindOfClass:[NSString class]]) {
        data = [NSJSONSerialization JSONObjectWithData:[(NSString *)dataObject dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:nil];
    }
    
    //大并发教室
    if ([msgName isEqualToString:@"BigRoom"]) {
        self.bigRoom = YES;
        return;
    }
    
    //小白板隐藏
    if (isDel) {
        if ([msgName isEqualToString:sBlackBoard_new]) {
            self.hidden = YES;
            [self clear];
            [_prepareData removeAllObjects];
            return;
        }
    }
    
    if ([msgName isEqualToString:sBlackBoard_new]) {
        //小白板状态
        //_prepareing       准备
        //_dispenseed       分发
        //_recycle          收回
        //_againDispenseed  再次分发
        NSString *blackBoardState = [data objectForKey:sBlackBoardState];
        NSString *currentTapKey = [data objectForKey:sCurrentTapKey];
        
        //状态切换以及页签切换
        if ([blackBoardState isEqualToString:s_Prepareing]) {
            //修改小白板状态为TKMiniWhiteBoardStatePrepareing，此状态下老师绘制全部保存，当分发时有学生加进来则将_prepareData绘制到学生上。
            //最终结果就是老师在TKMiniWhiteBoardStatePrepareing状态下的绘制将同步到所有学生。
            [self switchStates:TKMiniWhiteBoardStatePrepareing];
            
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                [_tkDrawView setWorkMode:TKWorkModeViewer];
                TKStudentSegmentObject *student = [[TKStudentSegmentObject alloc] init];
                student.ID = [TKEduSessionHandle shareInstance].localUser.peerID;
                student.nickName = [TKEduSessionHandle shareInstance].localUser.nickName;
                [self chooseStudent:student];
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
                [self show];
                [_tkDrawView setWorkMode:TKWorkModeControllor];
                [_tkDrawView switchToFileID:sBlackBoardCommon pageID:1 refreshImmediately:YES];
                [self chooseStudent:[TKStudentSegmentObject teacher]];
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
                [self show];
                [_tkDrawView switchToFileID:sBlackBoardCommon pageID:1 refreshImmediately:YES];
                [self chooseStudent:[TKStudentSegmentObject teacher]];
            }
            
        }
        if ([blackBoardState isEqualToString:s_Dispenseed]) {
            [self show];
            [self switchStates:TKMiniWhiteBoardStateDispenseed];
            //分发，学生端只显示自己画布
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                [_tkDrawView setWorkMode:TKWorkModeControllor];
                [_tkDrawView switchToFileID:[TKEduSessionHandle shareInstance].localUser.peerID pageID:1 refreshImmediately:YES];
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
                //老师切换切换画布
                [_tkDrawView setWorkMode:TKWorkModeControllor];
                [_tkDrawView switchToFileID:currentTapKey pageID:1 refreshImmediately:YES];
                if ([currentTapKey isEqualToString:sBlackBoardCommon]) {
                    [_tkDrawView setWorkMode:TKWorkModeControllor];
                } else {
                    [_tkDrawView setWorkMode:TKWorkModeViewer];
                }
            }
            
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
                [_tkDrawView switchToFileID:currentTapKey pageID:1 refreshImmediately:YES];
                TKStudentSegmentObject *student = [[TKStudentSegmentObject alloc] init];
                student.ID = currentTapKey;
                student.currentPage = 1;
                [self chooseStudent:student];
            }
            
        }
        if ([blackBoardState isEqualToString:s_AgainDispenseed]) {
            [self show];
            [self switchStates:TKMiniWhiteBoardStateAgainDispenseed];
            //再次分发，学生端只显示自己画布
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                [_tkDrawView switchToFileID:[TKEduSessionHandle shareInstance].localUser.peerID pageID:1 refreshImmediately:YES];
                [_tkDrawView setWorkMode:TKWorkModeControllor];
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
                TKStudentSegmentObject *student = [[TKStudentSegmentObject alloc] init];
                student.ID = currentTapKey;
                student.currentPage = 1;
                [_tkDrawView setWorkMode:TKWorkModeControllor];
                [self chooseStudent:student];
                [_tkDrawView switchToFileID:currentTapKey pageID:1 refreshImmediately:YES];
                if ([currentTapKey isEqualToString:sBlackBoardCommon]) {
                    [_tkDrawView setWorkMode:TKWorkModeControllor];
                } else {
                    [_tkDrawView setWorkMode:TKWorkModeViewer];
                }
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
                TKStudentSegmentObject *student = [[TKStudentSegmentObject alloc] init];
                student.ID = currentTapKey;
                student.currentPage = 1;
                [self chooseStudent:student];
                [_tkDrawView switchToFileID:currentTapKey pageID:1 refreshImmediately:YES];
            }
        }
        
        if ([blackBoardState isEqualToString:s_Recycle]) {
            [self show];
            [self switchStates:TKMiniWhiteBoardStateRecycle];
            //回收，显示所有画布，根据currentTapKey选择显示的画布，blackBoardCommon代表老师
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                TKStudentSegmentObject *student = [[TKStudentSegmentObject alloc] init];
                student.ID = currentTapKey;
                student.currentPage = 1;
                [self chooseStudent:student];
                [_tkDrawView switchToFileID:student.ID pageID:student.currentPage refreshImmediately:YES];
                [_tkDrawView setWorkMode:TKWorkModeViewer];
                [_selectorView removeFromSuperview];
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
                TKStudentSegmentObject *student = [[TKStudentSegmentObject alloc] init];
                student.ID = currentTapKey;
                student.currentPage = 1;
                [self chooseStudent:student];
                [_tkDrawView setWorkMode:TKWorkModeControllor];
                [_tkDrawView switchToFileID:currentTapKey pageID:1 refreshImmediately:YES];
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
                TKStudentSegmentObject *student = [[TKStudentSegmentObject alloc] init];
                student.ID = currentTapKey;
                student.currentPage = 1;
                [self chooseStudent:student];
                [_tkDrawView switchToFileID:currentTapKey pageID:1 refreshImmediately:YES];
            }
        }
    }
    
    if ([associatedMsgID isEqualToString:sBlackBoard_new]) {
        //绘制
        if ([msgName isEqualToString:sSharpsChange]) {
            NSString *fileID = [data objectForKey:sWhiteboardID];
            
            NSNumber *isBaseboard = [data objectForKey:@"isBaseboard"];
            if (isBaseboard.boolValue) {
                //主画布数据需要同步到每个添加进来的学生
                [_prepareData addObject:data];
            }
            
            
            [_tkDrawView switchToFileID:fileID pageID:1 refreshImmediately:[fileID isEqualToString:_choosedStudent.ID]];
            [_tkDrawView addDrawData:data refreshImmediately:[fileID isEqualToString:_choosedStudent.ID]];
            
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                //学生分发状态只显示自己画布
                if (_state == TKMiniWhiteBoardStateDispenseed || _state == TKMiniWhiteBoardStateAgainDispenseed || _state == TKMiniWhiteBoardStatePrepareing) {
                    [_tkDrawView switchToFileID:[TKEduSessionHandle shareInstance].localUser.peerID pageID:1 refreshImmediately:YES];
                }
            }
            if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
                [_tkDrawView switchToFileID:self.choosedStudent.ID pageID:1 refreshImmediately:[fileID isEqualToString:self.choosedStudent.ID]];
            }
        }
        
        //新进角色
        if ([msgName isEqualToString:sUserHasNewBlackBoard]) {
            
            TKStudentSegmentObject *obj = [[TKStudentSegmentObject alloc] initWithDictionary:data];
            BOOL addRestult = [self addStudent:obj];
            
            //创建收到的新学生白板
            if (!isDel) {
                if (addRestult) {
                    [_prepareData enumerateObjectsUsingBlock:^(NSDictionary *_Nonnull data, NSUInteger idx, BOOL * _Nonnull stop) {
                        [_tkDrawView switchToFileID:obj.ID pageID:obj.currentPage refreshImmediately:NO];
                        [_tkDrawView addDrawData:data refreshImmediately:NO];
                    }];
                    
                    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                        if (_state == TKMiniWhiteBoardStateDispenseed || _state == TKMiniWhiteBoardStateAgainDispenseed) {
                            //分发再次分发状态都切换到自己画布
                            [_tkDrawView switchToFileID:[TKEduSessionHandle shareInstance].localUser.peerID pageID:1 refreshImmediately:YES];
                        } else {
                            [_tkDrawView switchToFileID:_choosedStudent.ID pageID:1 refreshImmediately:YES];
                        }
                    }
                    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher || [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
                        [_tkDrawView switchToFileID:sBlackBoardCommon pageID:1 refreshImmediately:YES];
                    }
                }
            } else {
                [self removeStudent:obj];
                //删除正在显示的student
                if ([obj.ID isEqualToString:_choosedStudent.ID]) {
                    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher) {
                        // 老师重新指定 当前标签
                        [self didSelectStudent:[TKStudentSegmentObject teacher]];
                        
                    } else if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
                        
                        [_tkDrawView switchToFileID:sBlackBoardCommon pageID:1 refreshImmediately:YES];
                        [self chooseStudent:[TKStudentSegmentObject teacher]];
                        
                    } else if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
                        
                        if (_state == TKMiniWhiteBoardStateRecycle) {
                            [_tkDrawView switchToFileID:sBlackBoardCommon pageID:1 refreshImmediately:YES];
                            [self chooseStudent:[TKStudentSegmentObject teacher]];
                        }
                    }
                }
            }
        }
    }
}

- (void)show
{
    if (self.isBigRoom && [TKEduSessionHandle shareInstance].localUser.publishState == 0) {
        //大并发教室学生未上台，不显示小白板
        self.hidden = YES;
        return;
    }
    
    [self.superview bringSubviewToFront:self];
    self.hidden = NO;
}

@end
