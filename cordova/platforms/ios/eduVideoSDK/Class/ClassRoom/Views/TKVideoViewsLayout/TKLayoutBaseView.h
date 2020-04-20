//
//  TKLayoutBaseView.h
//  EduClass
//
//  Created by maqihan on 2019/4/12.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class TKCTVideoSmallView;
@interface TKLayoutBaseView : UIView

//用于展示用户的全部小视频
@property (strong , nonatomic,nullable) NSArray <TKCTVideoSmallView *>*videoArray;


@end

NS_ASSUME_NONNULL_END
