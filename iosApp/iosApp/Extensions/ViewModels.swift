//
//  ViewModels.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 05.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI

@dynamicMemberLookup
public class ObservableViewModel<VM: shared.ViewModel>: ObservableObject {
    private(set) var viewModel: VM

    init(_ viewModel: VM) {
        self.viewModel = viewModel
    }

    // Reference: https://www.swiftbysundell.com/tips/combining-dynamic-member-lookup-with-key-paths/

    subscript<T>(dynamicMember keyPath: KeyPath<VM, T>) -> T {
        viewModel[keyPath: keyPath]
    }

    subscript<T>(dynamicMember keyPath: WritableKeyPath<VM, T>) -> T {
        get { viewModel[keyPath: keyPath] }
        set { viewModel[keyPath: keyPath] = newValue }
    }

    deinit {
        viewModel.clear()
    }
}
