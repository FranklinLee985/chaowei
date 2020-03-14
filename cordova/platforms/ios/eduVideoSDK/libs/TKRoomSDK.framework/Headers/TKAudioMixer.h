//
//  TKAudioMixer.h
//  TKRoomSDK
//
//  Created by MAC-MiNi on 2018/10/22.
//  Copyright © 2018年 MAC-MiNi. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TKAudioInfo : NSObject
@property (nonatomic) int bytes_per_sample;
@property (nonatomic) int sample_rate;
@property (nonatomic) int number_of_channels;
@property (nonatomic) int number_of_frames;
@property (nonatomic) int fromat;
@end

@class TKAudioMixer;
@protocol TKAudioMixerOuputDelegate<NSObject>
-(void)mixedAudioOutput:(TKAudioMixer *)mixer ouput_data:(const void *)data audioInfo:(TKAudioInfo *)audioInfo;

@end

@interface TKAudioMixer : NSObject
- (instancetype)initWithDelegate:(id<TKAudioMixerOuputDelegate>)delegate audioInfo:(TKAudioInfo *)audioInfo;
- (int)addSource:(NSString *)sid;
- (int)removeSource:(NSString *)sid;
- (int)receiveData:(NSString *)sid audio_data:(void *)audio_data audioInfo:(TKAudioInfo *)audioInfo;
@end
