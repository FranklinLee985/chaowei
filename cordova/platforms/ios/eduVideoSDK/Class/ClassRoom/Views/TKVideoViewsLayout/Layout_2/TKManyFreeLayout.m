//
//  TKManyFreeLayout.m
//  EduClass
//
//  Created by maqihan on 2019/4/14.
//  Copyright © 2019 talkcloud. All rights reserved.
//

#import "TKManyFreeLayout.h"

@interface TKManyFreeLayout()

@end

@implementation TKManyFreeLayout

-(void)prepareLayout
{
    [super prepareLayout];
}

-(CGSize)collectionViewContentSize
{
    return [self collectionView].frame.size;
}

- (UICollectionViewLayoutAttributes *)layoutAttributesForItemAtIndexPath:(NSIndexPath *)path
{
    UICollectionViewLayoutAttributes* attributes = [super layoutAttributesForItemAtIndexPath:path];
    
    NSInteger count = [[self collectionView] numberOfItemsInSection:0];
    
    if (count == 3 || count == 5) {
        attributes.size   = [self sizeForCell];
        attributes.center = [self centerForCellWithIndexPath:path];
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

 - (CGSize)sizeForCell
{
    NSInteger count = [[self collectionView] numberOfItemsInSection:0];
    CGSize size = CGSizeZero;
    CGFloat width  = CGRectGetWidth(self.collectionView.frame);
    CGFloat height = CGRectGetHeight(self.collectionView.frame);

    if (count == 3) {
        size = CGSizeMake(4.0/3 * height/2, height/2);
    }else if (count == 5){
        size = CGSizeMake(width/3, 3.0/4*width/3);
    }
    return size;
}

- (CGPoint)centerForCellWithIndexPath:(NSIndexPath *)indexPath
{
    CGPoint center = CGPointZero;
    CGFloat width  = CGRectGetWidth(self.collectionView.frame);
    CGFloat height = CGRectGetHeight(self.collectionView.frame);
    
    NSInteger count = [[self collectionView] numberOfItemsInSection:0];

    switch (count) {
        case 3:
        {
            if (indexPath.row == 0) {
                center = CGPointMake(width/2, height/4);
                
            }else if (indexPath.row == 1){
                center = CGPointMake(width/2 - [self sizeForCell].width / 2, height*3/4);
                
            }else if (indexPath.row == 2){
                center = CGPointMake(width/2 + [self sizeForCell].width / 2, height*3/4);
            }
        }
            break;
            
        default:
        {
            if (indexPath.row == 0) {
                center = CGPointMake(width/2 - [self sizeForCell].width / 2, height/2 - [self sizeForCell].height / 2);
                
            }else if (indexPath.row == 1){
                center = CGPointMake(width/2 + [self sizeForCell].width / 2, height/2 - [self sizeForCell].height / 2);

            }else if (indexPath.row == 2){
                center = CGPointMake([self sizeForCell].width / 2, height/2 + [self sizeForCell].height / 2);
                
            }else if (indexPath.row == 3){
                center = CGPointMake(width / 2, height/2 + [self sizeForCell].height / 2);

            }else if (indexPath.row == 4){
                center = CGPointMake(width / 2 + [self sizeForCell].width, height/2 + [self sizeForCell].height / 2);
            }
        }
            break;
    }
    

    return center;
}

@end
