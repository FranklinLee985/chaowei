//
//  TKManyNormalLayout.m
//  EduClass
//
//  Created by maqihan on 2019/4/18.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKManyNormalLayout.h"

@interface TKManyNormalLayout()
//实际的间隔 因为有floor函数的执行，导致cell的布局过程中 间隔会发生改变
//@property (assign , nonatomic) CGFloat realSpace;

@property (assign , nonatomic) CGSize teacherSize;

@end

@implementation TKManyNormalLayout

- (UICollectionViewLayoutAttributes *)layoutAttributesForItemAtIndexPath:(NSIndexPath *)path
{
    UICollectionViewLayoutAttributes *attributes = [super layoutAttributesForItemAtIndexPath:path];

    if (path.item == 0 && self.haveTeacher) {
        
        self.teacherSize = attributes.size;
        return attributes;
    }
    
    if (_haveTeacher) {
        
        CGRect frame = attributes.frame;
        frame.origin.y = path.item < 13 ? 0 : attributes.size.height + 3;
        
        if (path.item > 12) {
            frame.origin.x =  attributes.frame.origin.x + self.teacherSize.width + 3;
        }else{
            NSIndexPath *indexPath = [NSIndexPath indexPathForItem:path.item - 1 inSection:0];
            UICollectionViewLayoutAttributes *preAttributes = [self layoutAttributesForItemAtIndexPath:indexPath];
            frame.origin.x =  CGRectGetMaxX(preAttributes.frame) + 3;
        }
        attributes.frame = frame;
        
    }else{
        
        CGRect frame = attributes.frame;
        frame.origin.y = path.item <= 13 ? 0 : attributes.size.height + 3;
        attributes.frame = frame;
    }

    return attributes;
}


-(NSArray*)layoutAttributesForElementsInRect:(CGRect)rect
{
    NSMutableArray* attributes = [NSMutableArray array];
    NSInteger count = [[self collectionView] numberOfItemsInSection:0];
    
    for (NSInteger i=0 ; i < count; i++) {
        NSIndexPath* indexPath = [NSIndexPath indexPathForItem:i inSection:0];
        [attributes addObject:[self layoutAttributesForItemAtIndexPath:indexPath]];
    }
    return attributes;
}
@end
