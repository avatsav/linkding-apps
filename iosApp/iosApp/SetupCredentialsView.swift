//
//  SetupCredentialsView.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 17.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SetupCredentialsView: View {
        
    @State var hostUrl: String = ""
    @State var apiKey: String = ""
    
    var body: some View {
        NavigationView() {
            VStack(alignment:.leading, spacing: 16) {
                Text("Enter details and start bookmarking")
                Spacer().frame(height: 16)
                TextField("Linkding Host URL", text: $hostUrl)
                    .padding()
                    .overlay(
                        RoundedRectangle(cornerRadius:10.0)
                            .strokeBorder(
                                Color.gray,
                                style:StrokeStyle(lineWidth:1.0)
                            )
                    )
                    .padding(.bottom,16)
                TextField("Api Key", text: $apiKey)
                    .padding()
                    .overlay(
                        RoundedRectangle(cornerRadius:10.0)
                            .strokeBorder(
                                Color.gray,
                                style:StrokeStyle(lineWidth:1.0)
                            )
                    )
                Spacer().frame(height: 16)
                HStack(spacing: 12) {
                    Button(action: {print("Button tapped")}) {
                        Text("Let's go")
                            .font(.headline)
                            .foregroundColor(.white)
                            .padding()
                            .frame(width: 120, height: 40)
                            .background(Color.blue)
                            .cornerRadius(5.0)
                    }
                    ProgressView()
                }
                Spacer()
            }
            .padding()
            .navigationTitle("Hello there!")
            .navigationBarTitleDisplayMode(.large)
        }
    }
}


struct SetupCredentialsView_Previews: PreviewProvider {
    static var previews: some View {
        SetupCredentialsView()
    }
}
