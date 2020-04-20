//
//  TKEmojiHeader.h
//  EduClass
//
//  Created by lyy on 2017/11/16.
//  Copyright © 2017年 beijing. All rights reserved.
//

#ifndef TKEmojiHeader_h
#define TKEmojiHeader_h

#import "TKEmotionTextView.h"
#import "TKEmotion.h"
#import "TKEmotionKeyboard.h"
// 表情的最大行数
#define TKEmotionMaxRows 3
// 表情的最大列数
#define TKEmotionMaxCols 14
// 每页最多显示多少个表情
#define TKEmotionMaxCountPerPage (TKEmotionMaxRows * TKEmotionMaxCols - 1)
// 通知
// 表情选中的通知
#define TKEmotionDidSelectedNotification @"TKEmotionDidSelectedNotification"
// 点击删除按钮的通知
#define TKEmotionDidDeletedNotification @"TKEmotionDidDeletedNotification"
// 通知里面取出表情用的key
#define TKSelectedEmotion @"TKSelectedEmotion"

#define TKKeyBoardHeight 160 //键盘高度，可以根据表情多少进行修改调整

#endif /* TKEmojiHeader_h */
