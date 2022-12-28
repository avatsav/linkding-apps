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

extension IosDependencies {
    private static let presenterKeyPaths: [PartialKeyPath<IosDependencies>] = [
        \.setupConfigurationPresenter,
        \.mainPresenter
    ]

    private static let allKeyPaths: [PartialKeyPath<IosDependencies>] = presenterKeyPaths

    public func get<T>() -> T {
        for partialKeyPath in Self.allKeyPaths {
            guard let keyPath = partialKeyPath as? KeyPath<IosDependencies, T> else { continue }
            return self[keyPath: keyPath]
        }
        fatalError("\(T.self) is not registered. Register dependency in both Kotlin and here in the keypaths.")
    }
}

public enum KoinSwift {
    private static let koin = KoinKt.initializeKoin()
    private static let dependencies = IosDependencies(koin: koin)

    @discardableResult
    static func start() -> Koin {
        koin
    }

    static func get<T>() -> T {
        dependencies.get()
    }
}

@propertyWrapper
public struct KoinInject<T> {
    public lazy var wrappedValue: T = KoinSwift.get()
    public init() {}
}
