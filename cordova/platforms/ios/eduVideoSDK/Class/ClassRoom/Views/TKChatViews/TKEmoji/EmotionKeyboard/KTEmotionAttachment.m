//
//  HMEmotionAttachment.m
//  EduClass
//
//  Created by lyy on 2017/11/16.
//  Copyright © 2017年 beijing. All rights reserved.

//#import "XDYMoreConfig.h"
#import "KTEmotionAttachment.h"
#import "TKEmotion.h"

@implementation KTEmotionAttachment

- (void)setEmotion:(TKEmotion *)emotion
{
    _emotion = emotion;
    
    self.image = [UIImage imageNamed:[NSString stringWithFormat:@"%@/%@", emotion.directory, emotion.png]];
}

@end
