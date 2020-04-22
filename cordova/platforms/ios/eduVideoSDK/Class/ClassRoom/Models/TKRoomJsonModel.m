//
//  TKRoomModel.m
//  TKRoomSDK
//
//  Created by MAC-MiNi on 2018/10/19.
//  Copyright © 2018年 MAC-MiNi. All rights reserved.
//

#import "TKRoomJsonModel.h"

@implementation TKRoomJsonModel

- (instancetype)initWithDictionary:(NSDictionary *)dic isPlayback:(BOOL)isPlayback {
    
    if (self = [super init]) {
        self.isPlayback = isPlayback;
        [self setValuesForKeysWithDictionary:dic];
        if (self.chairmancontrol) {
            _configuration = [[TKRoomConfiguration alloc] initWithConfigurationString:self.chairmancontrol isPlayback:_isPlayback];
        }
    }
    return self;
}

- (void)setValue:(id)value forKey:(NSString *)key
{
    if ([key isEqualToString:@"roomlayout"]) {
        
        if ([value isKindOfClass:NSString.class]) {
            
            _roomlayout = ((NSString *)value).intValue;
           
        }
        else {
            _roomlayout = (int)value;
        }
    }
    [super setValue:value forKey:key];
}
- (void)setValue:(id)value forUndefinedKey:(nonnull NSString *)key
{
    if ([key isEqualToString:@"serial"]) {
        
        self.roomid = value;
    } else {
    
//        TKLog(@"SET TKRoomModel - not find key     - %@ for value - %@", key, value);
    }
}

@end


@implementation TKRoomConfiguration

- (instancetype)initWithConfigurationString: (NSString *)configurationString isPlayback:(BOOL)isPlayback {
    
    if (!configurationString || configurationString.length == 0) {
        return nil;
    }
    
    if (self = [super init]) {
        
        _cString     = configurationString;
        _isPlayBack = isPlayback;
        
        self.autoQuitClassWhenClassOverFlag 	= [self cutOutStringWithIndex: 7];
        self.autoOpenAudioAndVideoFlag 			= [self cutOutStringWithIndex: 23];
        self.autoStartClassFlag 				= [self cutOutStringWithIndex: 32];
        self.allowStudentCloseAV 				= [self cutOutStringWithIndex: 33];
        self.hideClassBeginEndButton 			= [self cutOutStringWithIndex: 34];
        self.assistantCanPublish 				= [self cutOutStringWithIndex: 36];
        self.canDrawFlag 						= [self cutOutStringWithIndex: 37];
        self.canPageTurningFlag 				= [self cutOutStringWithIndex: 38];
        self.beforeClassPubVideoFlag 			= [self cutOutStringWithIndex: 41];
        self.autoShowAnswerAfterAnswer          = [self cutOutStringWithIndex: 42];
        self.coursewareRemarkFlag 				= [self cutOutStringWithIndex: 43];
        self.forbidLeaveClassFlag 				= [self cutOutStringWithIndex: 47];
        self.customTrophyFlag 					= [self cutOutStringWithIndex: 44];
        self.videoWhiteboardFlag 				= [self cutOutStringWithIndex: 48];
        self.coursewareFullSynchronize 			= [self cutOutStringWithIndex: 50];
        self.pauseWhenOver 						= [self cutOutStringWithIndex: 52];
        self.isChatAllowSendImage               = [self cutOutStringWithIndex: 53];
        self.documentCategoryFlag 				= [self cutOutStringWithIndex: 56];
        self.isShowWriteUpTheName               = [self cutOutStringWithIndex: 58];
        self.endClassTimeFlag 					= [self cutOutStringWithIndex: 71];
        self.groupFlag				 			= [self cutOutStringWithIndex: 75];
        self.hideClassEndBtn 					= [self cutOutStringWithIndex: 78];
        self.canChangedToAudioOnly              = [self cutOutStringWithIndex: 80];
        self.whiteboardColorFlag 				= [self cutOutStringWithIndex: 81];
        self.isPromptAssistantJoinRoom          = [self cutOutStringWithIndex: 90];
        self.coursewarePreload					= [self cutOutStringWithIndex: 102];
        self.coursewareOpenInWhiteboard 		= [self cutOutStringWithIndex: 104];
        self.isHiddenPageFlip					= [self cutOutStringWithIndex: 112];
        self.shouldHideMouseOnDrawToolView      = [self cutOutStringWithIndex: 113];
        self.shouldHideShapeOnDrawToolView		= [self cutOutStringWithIndex: 114];
        self.shouldHideFontOnDrawSelectorView	= [self cutOutStringWithIndex: 115];
        self.sortSmallVideo 					= [self cutOutStringWithIndex: 116];
        self.unShowStudentNetState              = [self cutOutStringWithIndex: 117];
        self.onlyMeAndTeacherVideo 				= [self cutOutStringWithIndex: 119];
        self.isChineseJapaneseTranslation 		= [self cutOutStringWithIndex: 122];
        self.isPenCanPenetration                = [self cutOutStringWithIndex: 131];
        self.isHiddenKickOutStudentBtn          = [self cutOutStringWithIndex: 135];
    }
    
    return self;
}

- (BOOL)cutOutStringWithIndex:(NSInteger)index {

    if (_cString.length > index) {
        
        return [[_cString substringWithRange:NSMakeRange(index, 1)] isEqualToString:@"1"] ? YES : NO;
    }
    else return NO;
}
@end
