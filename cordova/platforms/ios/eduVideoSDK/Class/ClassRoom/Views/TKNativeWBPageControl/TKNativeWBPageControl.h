//
//  TKNativeWBPageControl.h
//  TKWhiteBoard
//
//  Created by 周洁 on 2018/12/27.
//  Copyright © 2018 MAC-MiNi. All rights reserved.
//	翻页控件

#import <UIKit/UIKit.h>


NS_ASSUME_NONNULL_BEGIN
@protocol TKNativeWBPageControlDelegate <NSObject>

- (void)prePage;						// 上一页
- (void)nextPage;						// 下一页
- (void)turnToPage:(NSNumber *)pageNum; // 跳转到某页
- (void)enlarge; 						// 放大
- (void)narrow; 						// 缩小
- (void)fullScreen:(BOOL)isSelected;	// 全屏

@end



@interface TKNativeWBPageControl : UIView<UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, weak)     id<TKNativeWBPageControlDelegate> whiteBoardControl;
@property (nonatomic, strong)   NSDictionary *remarkDict;   //备注内容
@property (nonatomic, assign)   BOOL  showMark;				// 是否显示课件备注
@property (nonatomic, assign)	BOOL  showZoomBtn;			// 是否显示放大缩小按钮
@property (nonatomic, assign)   float largeNarrowLevel;		// 缩放等级 0 ~ 4
@property (nonatomic, strong)   UIButton *leftArrow;    // 上一页
@property (nonatomic, strong)   UIButton *rightArrow;    // 下一页
@property (nonatomic, strong)   UIButton *fullScreen;    // 全屏
@property (nonatomic, assign)    BOOL  canDraw;
@property (nonatomic, assign, readonly) BOOL allowPaging;

- (void)resetBtnStates;
- (void)setTotalPage:(NSInteger)total currentPage:(NSInteger)currentPage;
//- (void)largeNarrowLevelChange:(NSInteger)level;
//初始化 设置上下翻页的状态
- (void)setup;
- (void)updateWithNSDictionary:(NSDictionary *)param;
- (instancetype)initWithHidePaging:(BOOL)isHide allowPaging:(BOOL)isAllow role:(TKUserRoleType)roleType;

@end



@interface TKNativePageTableView : UITableView

@end



@interface TKNativePageCell : UITableViewCell

- (void)setNumber:(NSInteger)number selected:(BOOL)selected;

@end


NS_ASSUME_NONNULL_END
