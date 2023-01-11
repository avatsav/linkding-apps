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

    public init(_ viewModel: VM) {
        self.viewModel = viewModel
    }

    // Reference: https://www.swiftbysundell.com/tips/combining-dynamic-member-lookup-with-key-paths/
    public subscript<T>(dynamicMember keyPath: KeyPath<VM, T>) -> T {
        viewModel[keyPath: keyPath]
    }

    public subscript<T>(dynamicMember keyPath: WritableKeyPath<VM, T>) -> T {
        get { viewModel[keyPath: keyPath] }
        set { viewModel[keyPath: keyPath] = newValue }
    }

    deinit {
        viewModel.clear()
    }
}

// Using property wrappers to solve the issues with method access from observable view model!
// Idea from KMM-ViewModel: https://github.com/rickclephas/KMM-ViewModel/blob/master/KMMViewModelSwiftUI/StateViewModel.swift
// TODO: Consider replacing all of this custom VM implementation with KMM-ViewModel
@propertyWrapper
public struct VMStateObject<VM: shared.ViewModel>: DynamicProperty {
    @StateObject private var observableViewModel: ObservableViewModel<VM>
    public var wrappedValue: VM { observableViewModel.viewModel }
    public var projectedValue: ObservableViewModel<VM> { observableViewModel }

    public init(wrappedValue: VM) {
        self._observableViewModel = StateObject(wrappedValue: ObservableViewModel(wrappedValue))
    }
}

@propertyWrapper
public struct VMObservedObject<VM: shared.ViewModel>: DynamicProperty {
    @ObservedObject private var observableObject: ObservableViewModel<VM>
    public var wrappedValue: VM { observableObject.viewModel }
    public var projectedValue: ObservableViewModel<VM> { observableObject }

    public init(_ projectedValue: ObservableViewModel<VM>) {
        self.observableObject = projectedValue
    }

    public init(wrappedValue: VM) {
        let observableObject = ObservableViewModel(wrappedValue)
        self.observableObject = observableObject
    }
}
