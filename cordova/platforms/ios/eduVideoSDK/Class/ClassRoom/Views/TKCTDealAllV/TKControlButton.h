//
//  TKControlButton.h
//  EduClass
//
//  Created by lyy on 2018/5/2.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TKControlButton : UIView

@property (nonatomic, assign)BOOL selected;
@property (nonatomic, assign)BOOL enable;

- (instancetype)initWithFrame:(CGRect)frame imageName:(NSString *)imageName disableImageName:(NSString *)disableImageName title:(NSString *)title;

-(void)controlAddTarget:(id)target action:(SEL)action;

@end
