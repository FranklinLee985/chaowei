//
//  TKVideoViewLayoutFactory.h
//  EduClass
//
//  Created by maqihan on 2019/4/12.
//  Copyright © 2019 talkcloud. All rights reserved.
//
/*
    1.视频播放与取消播放的所有操作都需要经过 TKVideoViewLayoutFactory 处理
    2.在TKVideoViewLayoutFactory中管理着三种布局，分别为 normalView ， speakerView ， freeView，通过设置属性viewLayout 来更换布局

 */
#import <Foundation/Foundation.h>

#import "TKManyNormalLayoutView.h"
#import "TKManySpeakerLayoutView.h"
#import "TKManyFreeLayoutView.h"

NS_ASSUME_NONNULL_BEGIN

@protocol TKVideoViewDelegate <NSObject>
@optional
//当前台上用户数变化时，会回调此方法，并返回当前台上的人数count
//此方法能告知白板什么时候改变frame
- (void)playingVideoWithUserCount:(NSInteger)count;

@end

@interface TKVideoViewLayoutFactory : NSObject

- (instancetype)initWithWhiteBoard:(UIView *)whiteBoard superview:(UIView *)superview;

@property (weak , nonatomic) id<TKVideoViewDelegate> delegate;

//保存play的视频数据
@property (strong , nonatomic) NSMutableArray <TKCTVideoSmallView *>*videoArray;

//切换布局后 需要设置viewLayout
@property (assign , nonatomic) TKRoomLayout viewLayout;

//获取当前布局下的contentview（normalView ，speakerView ，freeView中的一个实例）
@property (weak , nonatomic , nullable) TKLayoutBaseView *contentView;

//播放视频
- (TKCTVideoSmallView *)playVideoWithUser:(TKRoomUser *)user;

//停止播放
- (void)unPlayVideoWithUserID:(NSString *)userID;

// 用户退出教室 判断是否是主讲退出教室
- (void) userLeftRoom:(NSString *)peerid;

/**
 清理视频 (断线重连,退出教室)
 */
- (void)unPlayAllViews;

//重置所有视频
- (void)resetAllVideoViews;

//配置normalView ，speakerView ，freeView在不同布局下的隐藏与显示
- (void)configContentViews;

//根据ID获取用户视频 如果为nil 则没有该用户
- (TKCTVideoSmallView *)videoViewWithUserID:(NSString *)userID;

//切换主讲
- (void)exchangeSpeakerWithUserID:(NSString *)userID;

//分屏
- (void)splitScreenWithInfo:(NSDictionary *)dict superview:(UIView *)superview;

//拖拽
- (void)dragVideoView:(TKCTVideoSmallView *)videoView left:(CGFloat)left top:(CGFloat)top superview:(UIView *)view;

//缩放
- (void)scaleVideoViewWithZoomRatio:(CGFloat)ratio userID:(NSString *)userID superview:(UIView *)superview;
    
//课件全屏 返回老师的video
- (TKCTVideoSmallView *)fullScreenShowTeacherVideoView:(BOOL)state superview:(UIView *)superview;

@end

NS_ASSUME_NONNULL_END
