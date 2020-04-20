//
//  TKDocmentDocModel.h
//  EduClassPad
//
//  Created by ifeng on 2017/5/31.
//  Copyright © 2017年 beijing. All rights reserved.
//

#import <Foundation/Foundation.h>
#pragma mark fileList
/*
 {
 active = 1;
 companyid = 10032;
 downloadpath = "/upload/20170808_144905_yzxrpmvq.mp4";
 dynamicppt = 0;
 fileid = 21751;
 filename = "httpv.youku.comv.mp4";
 filepath = "";
 fileprop = 0;
 fileserverid = 0;
 filetype = mp4;
 isconvert = 1;
 newfilename = "20170808_144905_yzxrpmvq.mp4";
 pagenum = 1;
 pdfpath = "";
 size = 17397022;
 status = 1;
 swfpath = "/upload/20170808_144905_yzxrpmvq.mp4";
 type = 0;
 uploadtime = "2017-08-08 14:49:05";
 uploaduserid = 100620;
 uploadusername = admin;
 }*/

@interface TKDocmentDocModel : NSObject
@property (nonatomic, strong) NSNumber *active;
@property (nonatomic, strong) NSNumber *animation;
@property (nonatomic, strong) NSNumber *companyid;
@property (nonatomic, copy)   NSString *downloadpath;
@property (nonatomic, copy)   NSString *fileid;
@property (nonatomic, copy)   NSString *filename;
@property (nonatomic, copy)   NSString *filepath;
@property (nonatomic, strong) NSNumber *fileserverid;
@property (nonatomic, copy)   NSString *filetype;
@property (nonatomic, copy)   NSString *fileurl;
@property (nonatomic, strong) NSNumber* isconvert;//NSInteger
@property (nonatomic, copy)   NSString *newfilename;
@property (nonatomic, strong) NSNumber *pagenum;
@property (nonatomic, copy)   NSString *pdfpath;
@property (nonatomic, strong) NSNumber *size;
@property (nonatomic, strong) NSNumber *status;
@property (nonatomic, copy)   NSString *swfpath;
@property (nonatomic, copy)   NSString *type;// 1 默认文档
@property (nonatomic, copy)   NSString *uploadtime;
@property (nonatomic, strong) NSNumber *uploaduserid;
@property (nonatomic, copy)   NSString *uploadusername;
@property (nonatomic, strong) NSNumber* currpage;//NSInteger
@property (nonatomic, strong) NSNumber* dynamicppt;//1 是原动态ppt 2.新的
@property (nonatomic, strong) NSNumber* pptslide;//1 当前页面
@property (nonatomic, strong) NSNumber* pptstep;//0 贞
@property (nonatomic, strong) NSString *action;//show
//0:表示普通文档　１－２动态ppt(1: 第一版动态ppt 2: 新版动态ppt ）  3:h5文档
@property (nonatomic, strong) NSNumber *fileprop;
@property (nonatomic, strong) NSNumber* steptotal;//总的
@property (nonatomic, strong) NSNumber *isShow;//是否查看
@property (nonatomic, strong) NSNumber* duration;//BOOl
@property (nonatomic, copy) NSString *notifyurl;
@property (nonatomic, copy) NSString *preloadingzip;
@property (nonatomic, strong) NSNumber  *isContentDocument;
/**
 区分文件类型 0：课堂  1：系统
 */
@property (nonatomic, strong) NSString *filecategory;

// 新加字段 不知到意思
@property (nonatomic, strong) NSString *convertor;
@property (nonatomic, strong) NSString *catalogid;

-(void)dynamicpptUpdate;
- (void)resetToDefault;
@end
