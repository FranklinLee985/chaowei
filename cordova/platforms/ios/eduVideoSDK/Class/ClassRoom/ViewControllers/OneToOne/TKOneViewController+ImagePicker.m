//
//  TKOneViewController+ImagePicker.m
//  EduClass
//
//  Created by maqihan on 2018/11/21.
//  Copyright © 2018 talkcloud. All rights reserved.
//

#import "TKOneViewController+ImagePicker.h"

@implementation TKOneViewController (ImagePicker)

-(void)chooseAction:(int)buttonIndex delay:(BOOL)delay
{
    if (buttonIndex == 0) {
        // 打开相册
        //资源类型为图片库
        self.iPickerController = [[TKImagePickerController alloc] init];
        [self.iPickerController.navigationBar setTitleTextAttributes:@{NSForegroundColorAttributeName : [UIColor whiteColor]}];
        self.iPickerController.navigationBar.tintColor = RGBCOLOR(255, 255, 255);
        self.iPickerController.navigationBar.barTintColor = RGBCOLOR(42, 180, 242);
        
        self.iPickerController.navigationBar.alpha = 1;
        self.iPickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        //设置选择后的图片可被编辑
        self.iPickerController.allowsEditing = false;
        
        self.iPickerController.delegate = self;
        if (delay) {
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [self presentViewController:self.iPickerController
                                   animated:true
                                 completion:nil];
            });
        } else {
            [self presentViewController:self.iPickerController
                               animated:true
                             completion:nil];
        }
        
    } else if (buttonIndex == 1) {
        //拍照
        UIImagePickerControllerSourceType sourceType = UIImagePickerControllerSourceTypeCamera;
        //判断是否有相机
        if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
            AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
            if (authStatus == AVAuthorizationStatusAuthorized) {
                
                self.iPickerController = [[TKImagePickerController alloc] init];
                self.iPickerController.sourceType = sourceType;
                self.iPickerController.delegate = self;
                [self presentViewController:self.iPickerController animated:true completion:^{}];
            } else {
                TKLog(@"该设备无摄像头");
                
                TKAlertView *alert = [[TKAlertView alloc]initWithTitle:@"" contentText:TKMTLocalized(@"Prompt.NeedCamera") confirmTitle:TKMTLocalized(@"Prompt.Sure")];
                [alert show];
                
            }
        } else {
            TKLog(@"该设备无摄像头");
            TKAlertView *alert = [[TKAlertView alloc]initWithTitle:@"" contentText:TKMTLocalized(@"Prompt.NeedCamera") confirmTitle:TKMTLocalized(@"Prompt.Sure")];
            [alert show];
        }
    }
    
}

#pragma mark - UIImagePickerControllerDelegate
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info{
    
    NSURL *imageURL = [info valueForKey:UIImagePickerControllerReferenceURL];
    UIImage *img;
    if (picker.allowsEditing)
        img = [info objectForKey:UIImagePickerControllerEditedImage];
    else
        img = [info objectForKey:UIImagePickerControllerOriginalImage];
    img = [UIImage tkFixOrientation:img];
    self.progress = 0;
    dispatch_async(dispatch_get_main_queue(), ^{
        [picker dismissViewControllerAnimated:YES completion:^{
   
            self.iPickerController = nil;
        }];
        
        //[HUD hide:YES];
        //HUD = nil;
        if (!self.uploadImageView) {
            
            self.uploadImageView = [[TKUploadImageView alloc] initWithImage:img];
            self.uploadImageView.frame = CGRectMake(0, 0, ScreenW, ScreenH);
            self.uploadImageView.layer.masksToBounds = YES;
            self.uploadImageView.layer.cornerRadius = 4;
            self.uploadImageView.layer.borderWidth = 2.f;
            self.uploadImageView.layer.borderColor = [[UIColor whiteColor] CGColor];
            self.uploadImageView.userInteractionEnabled = YES;
            //[self.view addSubview:_uploadImageView];
            self.uploadImageView.target = self;
            self.uploadImageView.action = @selector(cancelUpload);
            [self.uploadImageView setProgress:0];
            
        }
    });
    
    tk_weakify(self);
    ALAssetsLibraryAssetForURLResultBlock resultblock = ^(ALAsset *myasset) {
        
        //       昵称_入口_年-月-日_时_分_秒
        NSString *fileName  = [NSString stringWithFormat:@"%@_%@_%@.JPG",[TKEduSessionHandle shareInstance].localUser.nickName,sMobile, [TKUtil getCurrentDateTime]];
        
        NSData *imgData = UIImageJPEGRepresentation(img, 0.5);
        tk_strongify(weakSelf);
        
        NSNumber * imageUse;
        switch ([TKEduSessionHandle shareInstance].updateImageUseType) {
            case TKUpdateImageUseType_Document:
                imageUse = @1;
                break;
            case TKUpdateImageUseType_Message:
                imageUse = @0;
                break;
        }
        
        [TKEduNetManager uploadWithaHost:sHost
                                   aPort:sPort
                                  roomID:[TKEduClassRoom shareInstance].roomJson.roomid
                                imageUse:imageUse
                                fileData:imgData fileName:fileName fileType:@"JPG"
                                userName:[TKEduSessionHandle shareInstance].localUser.nickName
                                  userID:[TKEduSessionHandle shareInstance].localUser.peerID
                                delegate:strongSelf];
        
    };
    
    ALAssetsLibrary *assetslibrary = [[ALAssetsLibrary alloc] init];
    [assetslibrary assetForURL:imageURL
                   resultBlock:resultblock
                  failureBlock:^(NSError *error) {
                      TKLog(@"获取图片失败");
                      
                  }];
    
    
    
}
- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    
    [picker dismissViewControllerAnimated:YES completion:^{
        
        self.iPickerController = nil;
        [self refreshUI];
    }];
    
}


@end
