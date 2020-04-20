//
//  TKManyViewController+WhiteBoard.m
//  EduClass
//
//  Created by maqihan on 2018/11/20.
//  Copyright © 2018 talkcloud. All rights reserved.
//

#import "TKManyViewController+WhiteBoard.h"

@implementation TKManyViewController (WhiteBoard)

//界面更新回调
- (void)boardOnViewStateUpdate:(NSDictionary *)message
{
    if (!self.iSessionHandle.whiteBoardManager.isShowOnWeb) {
        NSString *fileTypeMark = [message objectForKey:@"fileTypeMark"];
        if ([fileTypeMark isEqualToString:@"showOnLocal"]) {
            NSDictionary *page = [message objectForKey:@"page"];
            NSString *totalPage = [page objectForKey:@"totalPage"];
            NSString *currentPage = [page objectForKey:@"currentPage"];
            [self.pageControl setTotalPage:totalPage.integerValue currentPage:currentPage.integerValue];
        }
        self.pageControl.showZoomBtn = YES;
        return;
    } else {
        NSDictionary *page = [message objectForKey:@"page"];
        NSString *totalPage = [page objectForKey:@"totalPage"];
        NSString *currentPage = [page objectForKey:@"currentPage"];
        [self.pageControl setTotalPage:totalPage.integerValue currentPage:currentPage.integerValue];
        self.pageControl.showZoomBtn = NO;
    }
    
    [self.navbarView updateView:message];
    
    if ([message.allKeys containsObject:@"scale"]) {
        // scale 0-3/4.0  1-9/16.0 其余的按 3/4.0
        CGFloat scale = [[message objectForKey:@"scale"] floatValue];
        CGFloat dpiOld = self.whiteBoardRatio;

        CGFloat dpi;
        if (scale == 2) {
            dpi = 0;
            self.coursewareRatio = [message[@"irregular"] floatValue];
        } else if (scale == 1) {
            dpi = 9/16.0;
            self.coursewareRatio = 16 / 9.0;
        } else {
            dpi = 3/4.0;
            self.coursewareRatio = 4 / 3.0;
        }

        self.whiteBoardRatio = dpi;

        if (![TKEduSessionHandle shareInstance].iIsFullState && dpiOld != dpi) {
            [self refreshWhiteBoard:NO];
        }
    }
    
    [self.pageControl updateWithNSDictionary:message];
}

#pragma mark - 白板控制
- (void)prePage
{
    [self.iSessionHandle wbSessionManagerPrePage];
    //翻页收起画笔工具
    if (self.brushToolView.hidden == NO) {
        
        [self.brushToolView hideSelectorView];
    }
}
- (void)nextPage
{
    [self.iSessionHandle wbSessionManagerNextPage];
    //翻页收起画笔工具
    if (self.brushToolView.hidden == NO) {
        
        [self.brushToolView hideSelectorView];
    }
}
- (void)turnToPage:(NSNumber *)pageNum
{
    [self.iSessionHandle wbSessionManagerTurnToPage:pageNum.intValue];
    //翻页收起画笔工具
    if (self.brushToolView.hidden == NO) {
        
        [self.brushToolView hideSelectorView];
    }
}

- (void)enlarge
{
    [self.iSessionHandle wbSessionManagerEnlarge];
}
- (void)narrow
{
    [self.iSessionHandle wbSessionManagerNarrow];
}

- (void)fullScreen:(BOOL)isSelected
{    
    if (self.iUserType != TKUserType_Teacher && self.isRemoteFullScreen) { // 学生 巡课 在收到全屏 不可操作
        return;
    }
    
    BOOL coursewareFullSynchronize = self.roomJson.configuration.coursewareFullSynchronize;
    if (self.iUserType == TKUserType_Teacher && coursewareFullSynchronize && self.iSessionHandle.isClassBegin) {
        
        // 老师需同步到其他端
        [self.iSessionHandle sessionHandleFullScreenSend:!isSelected];
    } else {
        
        // 本地 (学生 自己的操作 || 关闭课件全屏同步)
        self.pageControl.fullScreen.selected = isSelected = !isSelected;
        [[NSNotificationCenter defaultCenter] postNotificationName:sChangeWebPageFullScreen object:@(isSelected)];
    }
    
    //全屏结束放大状态并重置按钮状态
    [self.iSessionHandle wbSessionManagerResetEnlarge];
    [self.pageControl resetBtnStates];
    //    }
}
@end

