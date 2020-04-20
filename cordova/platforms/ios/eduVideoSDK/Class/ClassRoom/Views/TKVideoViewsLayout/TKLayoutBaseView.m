//
//  TKLayoutBaseView.m
//  EduClass
//
//  Created by maqihan on 2019/4/12.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKLayoutBaseView.h"
#import "TKCTVideoSmallView.h"

@implementation TKLayoutBaseView

- (void)setVideoArray:(NSArray<TKCTVideoSmallView *> *)videoArray
{
    //按userID 排序用户
    NSArray *array =  [videoArray sortedArrayUsingComparator:^NSComparisonResult(TKCTVideoSmallView *obj1, TKCTVideoSmallView *obj2) {
        
        return  [obj1.iPeerId compare:obj2.iPeerId];
    }];
    
    NSMutableArray *mArray = [NSMutableArray arrayWithArray:array];
    
    //老师始终在数组第一位置
    for (TKCTVideoSmallView * view in mArray) {

        if (view.iRoomUser && view.iRoomUser.role == TKUserType_Teacher) {
            
            NSInteger index = [mArray indexOfObject:view];
            
            if (index != 0) {
                [mArray removeObjectAtIndex:index];
                [mArray insertObject:view atIndex:0];
            }
            break;
        }
    }
    
    _videoArray = mArray;
}

@end
