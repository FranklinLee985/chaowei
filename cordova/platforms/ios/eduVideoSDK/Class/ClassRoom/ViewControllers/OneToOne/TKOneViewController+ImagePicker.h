//
//  TKOneViewController+ImagePicker.h
//  EduClass
//
//  Created by maqihan on 2018/11/21.
//  Copyright © 2018 talkcloud. All rights reserved.
//

#import "TKOneViewController.h"

NS_ASSUME_NONNULL_BEGIN

@interface TKOneViewController (ImagePicker)<UIImagePickerControllerDelegate,UINavigationControllerDelegate>

-(void)chooseAction:(int)buttonIndex delay:(BOOL)delay;


@end

NS_ASSUME_NONNULL_END
