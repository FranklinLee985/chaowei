//
//  TKToolsResponderView.m
//  EduClass
//
//  Created by talkcloud on 2019/1/8.
//  Copyright © 2019年 talkcloud. All rights reserved.
//

#import "TKToolsResponderView.h"
#import <UIImage+GIF.h>
#import "TKEduSessionHandle.h"

#define ThemeKP(args) [@"TKToolsBox.TKResponderView." stringByAppendingString:args]
#define viewHeight fminf(ScreenH / 2, 200)

@interface TKToolsResponderView ()

@property (nonatomic, strong) UIView * contentView;

@property (nonatomic, strong) UILabel  * tipLabel;
@property (nonatomic, strong) UIButton * sureButton;
@property (nonatomic, strong) UIButton * closeButton;

@property (nonatomic, strong) UIImageView * gifImageView;

@property (nonatomic, assign) BOOL isShow;
@property (nonatomic, assign) BOOL isBegin;
@property (nonatomic, strong) NSString * userAdmin;

@property (nonatomic, strong) NSDictionary * getUserDict;

@end

@implementation TKToolsResponderView

- (instancetype)init {
    
    self = [super init];
    if (self) {
        
//        CGFloat viewHeight = fminf(ScreenH / 3, 200);
        UIView * bgView = [[UIView alloc] init];
        bgView.backgroundColor = [UIColor clearColor];
        [self addSubview:bgView];
        self.contentView = bgView;
        
        [self.contentView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(@(viewHeight));
            make.width.equalTo(_contentView.mas_height).multipliedBy(1.2);
            make.centerX.equalTo(self.mas_centerX);
            make.centerY.equalTo(self.mas_centerY);
            make.edges.equalTo(self);
        }];
        
        // 背景图
        UIImageView * bgImage = [[UIImageView alloc] init];
        bgImage.sakura.image(ThemeKP(@"tk_res_bg"));
        [self.contentView addSubview:bgImage];
        
        [bgImage mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.and.top.equalTo(_contentView);
            make.width.and.height.equalTo(_contentView.mas_height);
        }];
        
        // 背景GIF
        UIImageView * gifImage = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, viewHeight - 2, viewHeight - 2)];
        [self.contentView addSubview:gifImage];
        
        NSString *str = [TKTheme stringWithPath:ThemeKP(@"tk_res_bg_gif")];
        NSURL *imgUrl = [TK_BUNDLE URLForResource:str withExtension:@"gif"];
        NSData * gifImageData = [NSData dataWithContentsOfURL:imgUrl];
        UIImage * image = [UIImage sd_imageWithGIFData:gifImageData];
        gifImage.image = image;
        self.gifImageView = gifImage;
        
        [_gifImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.and.top.equalTo(_contentView).offset(1);
            make.bottom.equalTo(_contentView).offset(-1);
            make.width.equalTo(_gifImageView.mas_height);
        }];
        
        UILabel * label = [[UILabel alloc] init];
        label.textAlignment = NSTextAlignmentCenter;
        label.font = TKFont(14);
        label.sakura.textColor(ThemeKP(@"tk_res_lab_textColor"));
        [self.contentView addSubview:label];
        self.tipLabel = label;
        
        [_tipLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(_contentView).offset(8);
            make.centerX.equalTo(bgView);
            make.centerY.equalTo(bgView.mas_bottom).multipliedBy(1.0/3);
            make.height.equalTo(@(30));
            make.width.equalTo(bgView.mas_height).offset(-16);
        }];
        
        
        UIButton * sBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        sBtn.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn"), UIControlStateNormal);
        sBtn.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor"), UIControlStateNormal);
        sBtn.titleLabel.font = TKFont(14);
        [sBtn addTarget:self action:@selector(sureButtonClick:) forControlEvents:UIControlEventTouchUpInside];
        [self.contentView addSubview:sBtn];
        self.sureButton = sBtn;
        
        [_sureButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(bgView).multipliedBy(0.17);
            make.width.equalTo(_sureButton.mas_height).multipliedBy(2.67);
            make.centerX.equalTo(_tipLabel);
            make.centerY.equalTo(bgView.mas_bottom).multipliedBy(2.0/3);
        }];
        
        
        if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Assistant ||
            [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher ||
            [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
            
            // 关闭按钮
            UIButton * cBtn = [UIButton buttonWithType:UIButtonTypeCustom];
            cBtn.sakura.image(ThemeKP(@"tk_res_close"), UIControlStateNormal);
            cBtn.sakura.image(ThemeKP(@"tk_res_close"), UIControlStateSelected);
            [cBtn addTarget:self action:@selector(closeButtonClick:) forControlEvents:UIControlEventTouchUpInside];
            [self.contentView addSubview:cBtn];
            self.closeButton = cBtn;
            
            [_closeButton mas_makeConstraints:^(MASConstraintMaker *make) {
                make.right.equalTo(bgView.mas_right).offset(-3);
                make.top.equalTo(bgView).offset(1);
                make.height.and.width.equalTo(bgView.mas_height).multipliedBy(0.2);
            }];
        } else {
            
            [_contentView mas_updateConstraints:^(MASConstraintMaker *make) {
                make.width.equalTo(_contentView.mas_height).multipliedBy(1.0);
            }];
        }
    }
    return self;
}

- (void) sureButtonClick:(UIButton *) sender {
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol) {
        return;// 巡课不许操作
    }
    
    sender.userInteractionEnabled = NO;
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
        
        NSDictionary * dict = @{@"userAdmin":[TKEduSessionHandle shareInstance].localUser.nickName,@"isClick":@YES};
        [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sQiangDaZhe
                                                             ID:[NSString stringWithFormat:@"%@_%@", sQiangDaZhe, [TKEduSessionHandle shareInstance].localUser.peerID]
                                                             To:sTellAll
                                                           Data:[TKUtil dictionaryToJSONString:dict]
                                                           Save:YES
                                                AssociatedMsgID:sQiangDaQiMesg AssociatedUserID:nil expires:0 completion:nil];
    } else {
        
        if (_isShow && !_isBegin) {
            
            //是否显示//开始抢答//如果有人抢答，显示抢到的用户名
            NSDictionary * dict = @{@"isShow":@YES,
                                    @"begin":@YES,
                                    @"userAdmin":@""
                                    };
            NSString * str = [TKUtil dictionaryToJSONString:dict];
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sQiangDaQi ID:sQiangDaQiMesg To:sTellAll Data:str Save:YES AssociatedMsgID:sClassBegin AssociatedUserID:nil expires:0 completion:nil];
            
            // 初始位置 发送是为了web端同步 iOS 接受没做处理
            NSDictionary * dragDic = @{@"percentLeft":@(0.5),
                                       @"percentTop":@(0.5),
                                       @"isDrag":@YES
                                       };
            NSString * dragstr = [TKUtil dictionaryToJSONString:dragDic];
            [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sResponderDrag ID:sResponderDrag To:sTellAll Data:dragstr Save:NO AssociatedMsgID:sQiangDaQiMesg AssociatedUserID:nil expires:0 completion:nil];
            
        } else if (_isShow && _isBegin) {
            
            [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sQiangDaQi ID:sQiangDaQiMesg To:sTellAll Data:@{} completion:^(NSError *error) {
                
                if (!error) {
                    
                    NSDictionary * dict = @{@"isShow":@YES,
                                            @"begin":@NO,
                                            @"userAdmin":@""
                                            };
                    NSString * str = [TKUtil dictionaryToJSONString:dict];
                    [[TKEduSessionHandle shareInstance] sessionHandlePubMsg:sQiangDaQi ID:sQiangDaQiMesg To:sTellAll Data:str Save:YES AssociatedMsgID:sClassBegin AssociatedUserID:nil expires:0 completion:nil];
                }
            }];
        }
    }
}


- (void) closeButtonClick:(UIButton *) sender {
    
    [[TKEduSessionHandle shareInstance] sessionHandleDelMsg:sQiangDaQi ID:sQiangDaQiMesg To:sTellAll Data:@{} completion:nil];
}
#pragma mark - send

#pragma mark - receive
- (void)receiveShowResponderViewWith:(NSDictionary *)dict {
    
    _isShow  = [[dict objectForKey:@"isShow"] boolValue];
    _isBegin = [[dict objectForKey:@"begin"] boolValue];
    _userAdmin = [dict objectForKey:@"userAdmin"];
    
    _getUserDict = nil;
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol ||
        [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Assistant ||
        [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher ||
        [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
    
        if (_isShow && !_isBegin) {
            self.hidden = NO;
            [self.sureButton setTitle:TKMTLocalized(@"Res.btn.start") forState:UIControlStateNormal];
            self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn"), UIControlStateNormal);
            self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor"), UIControlStateNormal);
            self.tipLabel.text = TKMTLocalized(@"Res.lab.start");
            self.tipLabel.font = TKFont(14);
            
            if ([TKRoomManager instance].localUser.role == TKUserType_Patrol) {
                //未开始
                [self.sureButton setTitle:TKMTLocalized(@"Res.unStarted") forState:UIControlStateNormal];
                self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn_un"), UIControlStateNormal);
                self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor_un"), UIControlStateNormal);
                self.tipLabel.text = TKMTLocalized(@"Res.lab.start");
                self.tipLabel.font = TKFont(14);
            }
            

        } else if (_isShow && _isBegin) {
            self.hidden = NO;
            [self.sureButton setTitle:TKMTLocalized(@"Res.btn.getting") forState:UIControlStateNormal];
            self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn"), UIControlStateNormal);
            self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor"), UIControlStateNormal);
            self.tipLabel.text = TKMTLocalized(@"Res.lab.getting");
            self.tipLabel.font = TKFont(14);
            [self performSelector:@selector(allReadyWithObject:) withObject:@(11) afterDelay:8];
            
            if ([TKRoomManager instance].localUser.role == TKUserType_Patrol) {
                //抢答中
                [self.sureButton setTitle:TKMTLocalized(@"Res.btn.getting") forState:UIControlStateNormal];
                self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn_un"), UIControlStateNormal);
                self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor_un"), UIControlStateNormal);
                self.tipLabel.text = TKMTLocalized(@"Res.lab.getting");
                self.tipLabel.font = TKFont(14);
            }

        } else if (!_isShow && !_isBegin) {
            //重新开始
            self.hidden = NO;
            [self.sureButton setTitle:TKMTLocalized(@"Res.btn.noget") forState:UIControlStateNormal];
            self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn"), UIControlStateNormal);
            self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor"), UIControlStateNormal);
            self.tipLabel.text = TKMTLocalized(@"Res.lab.noget");
            self.tipLabel.font = TKFont(14);
            
            if ([TKRoomManager instance].localUser.role == TKUserType_Patrol) {
                [self.sureButton setTitle:TKMTLocalized(@"Res.unStarted") forState:UIControlStateNormal];
                self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn_un"), UIControlStateNormal);
                self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor_un"), UIControlStateNormal);
                self.tipLabel.text = TKMTLocalized(@"Res.lab.noget");
                self.tipLabel.font = TKFont(14);
            }
        }
        
    } else if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
    
        if ([TKEduSessionHandle shareInstance].localUser.publishState != TKPublishStateNONE) {
            
            if (_isShow && _isBegin) {
                self.hidden = NO;
                [self.sureButton setTitle:TKMTLocalized(@"Res.readyget") forState:UIControlStateNormal];
                self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn"), UIControlStateNormal);
                self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor"), UIControlStateNormal);
                self.tipLabel.text = TKMTLocalized(@"Res.readyget");
                self.tipLabel.font = TKFont(14);
                self.sureButton.userInteractionEnabled = NO;
                [self performSelector:@selector(allReadyWithObject:) withObject:@(1) afterDelay:3];
                [self performSelector:@selector(viewJumpMove) withObject:nil afterDelay:1];
            } else if (!_isShow && !_isBegin) {
                self.hidden = NO;
          
            } else {
                self.hidden = YES;
            }

        } else {
            self.hidden = YES;
        }
        
    } else {
        self.hidden = YES;
    }
    
}

- (void) allReadyWithObject:(id)obj {
    
    NSInteger type = [obj integerValue];
    // student
    if (type == 1) {
        // ready
        [self.sureButton setTitle:TKMTLocalized(@"Res.btn.start") forState:UIControlStateNormal];
        self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn"), UIControlStateNormal);
        self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor"), UIControlStateNormal);
        self.tipLabel.text = TKMTLocalized(@"Res.lab.start");
        self.tipLabel.font = TKFont(14);
        self.sureButton.userInteractionEnabled = YES;
        [self performSelector:@selector(allReadyWithObject:) withObject:@(2) afterDelay:5];
    } else if (type == 2) {

        [self.sureButton setTitle:TKMTLocalized(@"Res.lab.noget") forState:UIControlStateNormal];
        self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn"), UIControlStateNormal);
        self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor"), UIControlStateNormal);
        self.tipLabel.text = TKMTLocalized(@"Res.lab.noget");
        self.tipLabel.font = TKFont(14);
        self.sureButton.userInteractionEnabled = NO;
        [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(viewJumpMove) object:nil];
        [self mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.center.equalTo(self.superview);
        }];
    }
   
    
    // teacher
    else if (type == 11) {
        
        self.sureButton.userInteractionEnabled = YES;
        [self.sureButton setTitle:TKMTLocalized(@"Res.btn.noget") forState:UIControlStateNormal];
        self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn"), UIControlStateNormal);
        self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor"), UIControlStateNormal);
        self.tipLabel.text = TKMTLocalized(@"Res.lab.noget");
        self.tipLabel.font = TKFont(14);
        
        if ([TKRoomManager instance].localUser.role == TKUserType_Patrol) {
            
            [self.sureButton setTitle:TKMTLocalized(@"Res.unStarted") forState:UIControlStateNormal];
            self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn_un"), UIControlStateNormal);
            self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor_un"), UIControlStateNormal);
            self.tipLabel.text = TKMTLocalized(@"Res.lab.noget");
            self.tipLabel.font = TKFont(14);
        }
    }
}

- (void) viewJumpMove {
    
    // 跳一跳
    // 随机中心点轨迹 抢答器不超出课件区， 较大屏幕上 中心点位于课件区内延1/4以内（手机屏幕小，无此限制）
    CGFloat startX = (1.2 * viewHeight / 2) / self.superview.width + 0.05;
    CGFloat startY = (viewHeight / 2) / self.superview.height + 0.05;
    startX = fmaxf(startX, 0.25);
    startY = fmaxf(startY, 0.25);
    CGFloat centerX = startX + (arc4random() % (NSInteger)(100 * (1 - 2 * startX))) / 100.0;
    CGFloat centerY = startY + (arc4random() % (NSInteger)(100 * (1 - 2 * startY))) / 100.0;
    [self mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.superview.mas_right).multipliedBy(centerX);
        make.centerY.equalTo(self.superview.mas_bottom).multipliedBy(centerY);
    }];
    
    [self performSelector:@selector(viewJumpMove) withObject:nil afterDelay:1];
}

- (void)receiveResponderUser:(NSDictionary *)dict peerid:(nonnull NSString *)peerid {
    
    if (_getUserDict) {
        return;
    }
    _getUserDict = dict;
    
    if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Patrol ||
        [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Assistant ||
        [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Teacher ||
        [TKEduSessionHandle shareInstance].localUser.role == TKUserType_Playback) {
        //抢中
        self.sureButton.userInteractionEnabled = YES;
        [self.sureButton setTitle:TKMTLocalized(@"Res.btn.t.get") forState:UIControlStateNormal];
        self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn_un"), UIControlStateNormal);
        self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor_un"), UIControlStateNormal);
        self.tipLabel.text = dict[@"userAdmin"];
        self.tipLabel.font = TKFont(18);
        [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(allReadyWithObject:) object:@(11)];
        
        if ([TKRoomManager instance].localUser.role == TKUserType_Patrol) {
            [self.sureButton setTitle:TKMTLocalized(@"Res.btn.t.get") forState:UIControlStateNormal];
            self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn_un"), UIControlStateNormal);
            self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor_un"), UIControlStateNormal);
            self.tipLabel.text = dict[@"userAdmin"];
            self.tipLabel.font = TKFont(18);
        }
        
    } else if ([TKEduSessionHandle shareInstance].localUser.role == TKUserType_Student) {
        
        [self.sureButton setTitle:TKMTLocalized(@"Res.btn.get") forState:UIControlStateNormal];
        self.sureButton.sakura.backgroundImage(ThemeKP(@"tk_res_surebtn_un"), UIControlStateNormal);
        self.sureButton.sakura.titleColor(ThemeKP(@"tk_resSure_titleColor_un"), UIControlStateNormal);
        if ([[TKEduSessionHandle shareInstance].localUser.peerID isEqualToString:peerid]) {
            self.tipLabel.text = TKMTLocalized(@"Res.lab.iget");
        } else {
            self.tipLabel.text = dict[@"userAdmin"];
        }
        self.tipLabel.font = TKFont(18);
        self.sureButton.userInteractionEnabled = NO;
        [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(allReadyWithObject:) object:@(1)];
        [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(allReadyWithObject:) object:@(2)];
        [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(viewJumpMove) object:nil];
        [self mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.center.equalTo(self.superview);
        }];
        
    } else {
        self.hidden = YES;
    }
}

- (void)removeFromSuperview {
    
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
    [super removeFromSuperview];
}

@end
