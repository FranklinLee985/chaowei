//
//  TKImagePickerController.m
//  EduClass
//
//  Created by lyy on 2018/5/22.
//  Copyright © 2018年 talkcloud. All rights reserved.
//

#import "TKImagePickerController.h"

@interface TKImagePickerController ()

@end

@implementation TKImagePickerController

- (void)viewDidLoad {
    [super viewDidLoad];
   
    NSLog(@"%f",self.view.frame.size.width);
    NSLog(@"%f",self.view.frame.size.height);
    
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark 横竖屏
-(UIInterfaceOrientation)preferredInterfaceOrientationForPresentation {
    return UIInterfaceOrientationPortrait;
}
- (UIInterfaceOrientationMask)supportedInterfaceOrientations{
    return UIInterfaceOrientationMaskPortrait;
}
- (BOOL)shouldAutorotate {
    return YES;
}

- (void)dealloc{
    NSLog(@"tkimagePicker dealloc");
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
