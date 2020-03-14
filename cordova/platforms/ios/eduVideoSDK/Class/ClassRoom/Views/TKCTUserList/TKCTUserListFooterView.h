//
//  TKCTUserListFooterView.h
//  EduClass
//
//  Created by talkcloud on 2018/10/15.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol TKCTUserListFooterViewDelegate <NSObject>
- (void)userListJumpPageNum:(int)pageNum;
@end

@interface TKCTUserListFooterView : UIView

@property (nonatomic, weak) id<TKCTUserListFooterViewDelegate> delegate;
@property (nonatomic, copy) void(^nextPage)();

@property (nonatomic, copy) void(^prePage)();

@property (strong, nonatomic) UIButton *nextBtn;

@property (strong, nonatomic) UIButton *preBtn;

@property (strong, nonatomic) UILabel *lineLabel;

@property (strong, nonatomic) UILabel *totalPage;

@property (strong, nonatomic) UITextField *currentPage;

@property (strong, nonatomic) UIView *currentpageView;


- (void)setCurrentPageNum:(NSInteger)pageNum;
- (void)setTotalNum:(NSInteger)totalNum;
- (void)destory;

@end
