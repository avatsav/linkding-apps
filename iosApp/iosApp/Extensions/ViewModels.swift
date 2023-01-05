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

@MainActor
@dynamicMemberLookup
public class ObservableViewModel<ViewModel: shared.ViewModel>: ObservableObject {
    private(set) var viewModel: ViewModel

    init(_ viewModel: ViewModel) {
        self.viewModel = viewModel
    }

    // Reference: https://www.swiftbysundell.com/tips/combining-dynamic-member-lookup-with-key-paths/
    subscript<T>(dynamicMember keyPath: KeyPath<ViewModel, T>) -> T {
        viewModel[keyPath: keyPath]
    }

    subscript<T>(dynamicMember keyPath: WritableKeyPath<ViewModel, T>) -> T {
        get { viewModel[keyPath: keyPath] }
        set { viewModel[keyPath: keyPath] = newValue }
    }

    deinit {
        viewModel.clear()
    }
}
