/*
 * GPUImage-x
 *
 * Copyright (C) 2017 Yijin Wang, Yiqian Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "MenuViewController.h"
#import "ImageSampleViewController.h"
#import "CameraSampleViewController.h"

@interface MenuViewController ()

@end

@implementation MenuViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"Menu";
}


#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SampleMenuCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"SampleMenuCell"];
        cell.textLabel.textColor = [UIColor blackColor];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
    
    
    switch ([indexPath row]) {
        case 0:
            cell.textLabel.text = @"Image Sample";
            break;
        case 1:
            cell.textLabel.text = @"Camera Sample";
            break;
        default:
            break;
    }
    
    return cell;
}


#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UIViewController* nextViewController = 0;
    switch ([indexPath row]) {
        case 0:
            NSLog(@"Image Sample");
            nextViewController = [[ImageSampleViewController alloc] init];
            break;
        case 1:
            NSLog(@"Camera Sample");
            nextViewController = [[CameraSampleViewController alloc] init];
            break;
        default:
            break;
    }
    
    if (nextViewController != 0)
        [self.navigationController pushViewController:nextViewController animated:YES];
}
@end
