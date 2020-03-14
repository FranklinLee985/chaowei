//
//  TKNotificationHeader.h
//  EduClass
//
//  Created by lyy on 2018/5/11.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#ifndef TKNotificationHeader_h
#define TKNotificationHeader_h

static NSNotificationName const sTapTableNotification = @"tapTableNotification";
static NSNotificationName const stouchMainPageNotification = @"touchMainPageNotification";
static NSNotificationName const sDocListViewNotification = @"docListViewNotification";
static NSNotificationName const sPluggInMicrophoneNotification = @"pluggInMicrophone";
static NSNotificationName const sUnunpluggingHeadsetNotification = @"ununpluggingHeadset";

static NSNotificationName const tkReceiveMessageNotification = @"receiveMessageNotification";//接收聊天消息通知

static NSNotificationName const tkUserListNotification = @"userListNotification";//人员更新通知

static NSNotificationName const tkClassBeginNotification = @"teacherControlClassBegin";
static NSNotificationName const sTKRoomViewControllerDisappear = @"sTKRoomViewControllerDisappear";// 房间销毁


#endif /* TKNotificationHeader_h */
