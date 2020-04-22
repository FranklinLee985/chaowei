//
//  TKOneMaskView.h
//  EduClass
//
//  Created by talkcloud on 2018/10/12.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TKOneMaskView : UIView

/** *  当前的用户 */
@property(strong,nonatomic)TKRoomUser *_Nullable iRoomUser;
/** *  是否分屏 */
@property(assign,nonatomic)BOOL isSplit;

@property (nonatomic, assign) NSInteger iVideoViewTag;
@property (nonatomic, strong) UILabel *nameLabel;//用户名称
@property (nonatomic, strong) UIButton *trophyButton;//奖杯
@property (nonatomic, strong) UIButton *trophyNumBtn;// 奖杯数量
@property (nonatomic, strong) UIImageView *handImageView;//举手
@property (nonatomic, strong) UIButton *muteButton;//音频展示按钮

- (void)endInBackGround:(BOOL)isInBackground;
//更改用户名
- (void)changeName:(NSString *)name;
//开启纯音频教室
- (void)inOnlyAudioRoom;
// 画中画的按钮显示隐藏
- (void) maskViewChangeForPicInPicWithisShow:(BOOL)isShow;

- (void)refreshUI;
- (void)refreshRaiseHandUI:(NSDictionary *)dict;

-(void)refreshVolume:(NSDictionary *)dict;
@end

NS_ASSUME_NONNULL_END
