package com.example.serviceradar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.serviceradar.ui.auth.LoginScreen
import com.example.serviceradar.ui.auth.RegisterScreen
import com.example.serviceradar.ui.auth.SplashScreen
import com.example.serviceradar.ui.landing.LandingScreen
import com.example.serviceradar.ui.onboarding.OnboardingScreen
import com.example.serviceradar.ui.provider.ProviderDashboardScreen
import com.example.serviceradar.ui.provider.ProviderEarningsScreen
import com.example.serviceradar.ui.provider.ProviderLocationSetupScreen
import com.example.serviceradar.ui.customer.CustomerHomeScreen
import com.example.serviceradar.ui.customer.NearbyProvidersMapScreen
import com.example.serviceradar.ui.admin.AdminDashboardScreen
import com.example.serviceradar.ui.customer.CustomerProfileScreen
import com.example.serviceradar.viewmodel.AuthViewModel
import com.example.serviceradar.viewmodel.CustomerViewModel
import com.example.serviceradar.viewmodel.ProviderViewModel
import com.example.serviceradar.viewmodel.ThemeViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val LANDING = "landing"
    const val CUSTOMER_HOME = "customer_home"
    const val PROVIDER_HOME = "provider_home"
    const val PROVIDER_EARNINGS = "provider_earnings"
    const val ADMIN_HOME = "admin_home"
    const val CUSTOMER_PROFILE = "customer_profile"
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val NEARBY_MAP = "nearby_map"
    const val PROVIDER_LOCATION_SETUP = "provider_location_setup"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    themeViewModel: ThemeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onResult = { role ->
                    val destination = when (role) {
                        "Provider" -> Routes.PROVIDER_HOME
                        "Admin"    -> Routes.ADMIN_HOME
                        null       -> Routes.LANDING
                        else       -> Routes.CUSTOMER_HOME
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onShowOnboarding = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LANDING) {
            LandingScreen(
                // ── Login/SignUp: keep Landing in back stack so back btn returns here
                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                },
                onSignUpClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onFindServicesClick = {
                    navController.navigate(Routes.LOGIN)
                },
                onBeAProviderClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        "Provider" -> Routes.PROVIDER_HOME
                        "Admin"    -> Routes.ADMIN_HOME
                        else       -> Routes.CUSTOMER_HOME
                    }
                    // After successful login, clear entire back stack — user shouldn't
                    // go back to Landing/Login after logging in
                    navController.navigate(destination) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { role ->
                    val destination = when (role) {
                        "Provider" -> Routes.PROVIDER_HOME
                        "Admin"    -> Routes.ADMIN_HOME
                        else       -> Routes.CUSTOMER_HOME
                    }
                    // After successful register, clear entire back stack
                    navController.navigate(destination) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CUSTOMER_HOME) {
            val customerViewModel: CustomerViewModel = viewModel()
            CustomerHomeScreen(
                customerViewModel = customerViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LANDING) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onProfile = {
                    navController.navigate(Routes.CUSTOMER_PROFILE)
                },
                onNearbyMap = {
                    navController.navigate(Routes.NEARBY_MAP)
                },
                themeViewModel = themeViewModel
            )
        }

        composable(Routes.CUSTOMER_PROFILE) {
            CustomerProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.NEARBY_MAP) {
            // FIXED: reuse same CustomerViewModel from CUSTOMER_HOME so location is shared
            val customerHomeEntry = remember(it) {
                navController.getBackStackEntry(Routes.CUSTOMER_HOME)
            }
            val customerViewModel: CustomerViewModel = viewModel(customerHomeEntry)
            val providers by customerViewModel.providers.collectAsState()
            val customerLocation by customerViewModel.customerLocation.collectAsState()

            NearbyProvidersMapScreen(
                providers = providers,
                customerLat = customerLocation?.first ?: 0.0,
                customerLon = customerLocation?.second ?: 0.0,
                onBack = { navController.popBackStack() },
                onBookProvider = { navController.popBackStack() }
            )
        }

        composable(Routes.PROVIDER_HOME) {
            var showProfileDialog by remember { mutableStateOf(false) }
            val providerViewModel: ProviderViewModel = viewModel()

            ProviderDashboardScreen(
                providerViewModel = providerViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LANDING) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onProfile = { showProfileDialog = true },
                onEarnings = {
                    navController.navigate(Routes.PROVIDER_EARNINGS)
                },
                onSetLocation = {
                    navController.navigate(Routes.PROVIDER_LOCATION_SETUP)
                },
                themeViewModel = themeViewModel
            )

            if (showProfileDialog) {
                val providerProfile = providerViewModel.providerProfile.collectAsState().value
                providerProfile?.let { profile ->
                    com.example.serviceradar.ui.provider.EditProfileDialog(
                        initialCategory = profile.category,
                        initialPrice = profile.price,
                        onSave = { category, price ->
                            providerViewModel.updateProfile(category, price)
                            showProfileDialog = false
                        },
                        onDismiss = { showProfileDialog = false }
                    )
                }
            }
        }

        composable(Routes.PROVIDER_LOCATION_SETUP) {
            val providerViewModel: ProviderViewModel = viewModel()
            val providerLocation by providerViewModel.providerLocation.collectAsState()

            ProviderLocationSetupScreen(
                initialLat = providerLocation?.first ?: 0.0,
                initialLon = providerLocation?.second ?: 0.0,
                onLocationSaved = { lat, lon ->
                    providerViewModel.saveProviderLocation(lat, lon)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROVIDER_EARNINGS) {
            ProviderEarningsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_HOME) {
            AdminDashboardScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LANDING) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                themeViewModel = themeViewModel
            )
        }
    }
}