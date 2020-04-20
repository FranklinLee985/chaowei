//
//  TKTurnOffCameraView.m
//  EduClass
//
//  Created by lyy on 2018/5/11.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKTurnOffCameraView.h"

#define ThemeKP(args) [@"ClassRoom.TKVideoView." stringByAppendingString:args]

@interface TKTurnOffCameraView()


@end

@implementation TKTurnOffCameraView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        _backImageView = [[UIImageView alloc]init];
        _backImageView.contentMode = UIViewContentModeScaleToFill;
        
        _backImageView.sakura.image(ThemeKP(@"videoTurnOffCameraImage"));
        
        [self addSubview:_backImageView];
        
        _iconImageView = [[UIImageView alloc]init];
        _iconImageView.contentMode = UIViewContentModeScaleAspectFit;
        _iconImageView.sakura.image(ThemeKP(@"videoBackStuImage"));
//        icon_teacher_one_to_one
//        icon_student_one_to_one
//        icon_camera_close
        [self addSubview:_iconImageView];
        
        
    }
    return self;
}
- (void)layoutSubviews{
    _backImageView.frame = CGRectMake(0, 0, self.width, self.height);
    _iconImageView.frame = CGRectMake(0, 0, self.height*0.5, self.height*0.5);
    _iconImageView.center = self.center;
    
}

- (void)setIVideoViewTag:(NSInteger)iVideoViewTag {
    if (iVideoViewTag == -2) {
        _backImageView.sakura.image(ThemeKP(@"videoStuTurnOffCameraImage"));
    }else {
        _backImageView.sakura.image(ThemeKP(@"videoTurnOffCameraImage"));
    }
}

- (void)setIRoomUser:(TKRoomUser *)iRoomUser {
    
}
/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
