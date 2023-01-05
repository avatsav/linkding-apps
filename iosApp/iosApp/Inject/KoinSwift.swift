//
//  Koin.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 23.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

public typealias Koin = Koin_coreKoin

extension ViewModelsContainer {
    private static let keyPaths: [PartialKeyPath<ViewModelsContainer>] = [
        \.homeViewModel,
        \.bookmarksViewModel,
        \.addBookmarkViewModel
    ]

    public func get<ViewModel>() -> ViewModel {
        for partialKeyPath in Self.keyPaths {
            guard let keyPath = partialKeyPath as? KeyPath<ViewModelsContainer, ViewModel> else { continue }
            return self[keyPath: keyPath]
        }
        fatalError("\(ViewModel.self) is not available for injection")
    }
}

public enum KoinSwift {
    private static let koin = KoinKt.initializeKoin()
    private static let viewModelsContainer = ViewModelsContainer(koin: koin)

    @discardableResult
    static func start() -> Koin {
        koin
    }

    static func inject<T>() -> T {
        viewModelsContainer.get()
    }
}
