//
//  TKManySpeakerLayoutView.h
//  EduClass
//
//  Created by maqihan on 2019/4/12.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKLayoutBaseView.h"

NS_ASSUME_NONNULL_BEGIN
/**
 主讲布局，老师视频在左边，学生都在右边
 */
@interface TKManySpeakerLayoutView : TKLayoutBaseView

// 主讲视频
@property (strong , nonatomic , nullable) TKCTVideoSmallView     *speaker;

@property (nonatomic, copy) NSString *peerID;

@end

NS_ASSUME_NONNULL_END
