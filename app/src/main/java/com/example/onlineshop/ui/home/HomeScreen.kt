package com.example.onlineshop.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.onlineshop.R
import com.example.onlineshop.core.util.UiEvent
import com.example.onlineshop.data_example.CategoryList
import com.example.onlineshop.domain.model.CategoryItem

import com.example.onlineshop.network.FlashSale
import com.example.onlineshop.network.FlashSaleProductsList
import com.example.onlineshop.network.Latest
import com.example.onlineshop.network.Product
import com.example.onlineshop.ui.components.BottomNavBar
import com.example.onlineshop.ui.components.HomeTopAppBar
import com.example.onlineshop.ui.components.ProfilePhotoContainer
import com.example.onlineshop.ui.components.SearchField
import com.example.onlineshop.ui.theme.OnlineShopTheme
import com.example.onlineshop.ui.theme.Poppins
import com.google.accompanist.pager.ExperimentalPagerApi
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit = {},
    onNavigate: (route: String, popBackStack: Boolean) -> Unit = { _, _ -> },
    navController: NavHostController
) {
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> onNavigate(it.route, it.popBackStack)
                is UiEvent.NavigateUp -> onNavigateUp()
                else -> {}
            }
        }
    }
    val isOrientationLandscape = LocalContext.current.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE
    HomeScreenUi(
        navController = navController,
        latestProductList = viewModel.state.latestProductList,
        profilePhotoImageBitmap = viewModel.state.profilePhoto,
        flashSaleProductsList = viewModel.state.flashSaleProductList,
        isOrientationLandscape = isOrientationLandscape,
        searchValue = viewModel.state.searchValue,
        onSearchValueChange = {viewModel.onEvent(HomeEvent.OnSearchValueChange(it))}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenUi(
    searchValue:String = "",
    onSearchValueChange:(String)-> Unit= {},
    isProfilePhotoLoading: Boolean = false,
    profilePhotoImageBitmap: ImageBitmap? = null,
    isOrientationLandscape: Boolean = false,
    latestProductList: List<com.example.onlineshop.network.Latest> = listOf(),
    flashSaleProductsList: List<com.example.onlineshop.network.FlashSale> = listOf(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        topBar =
        {
            HomeTopAppBar(
                isPhotoLoading = isProfilePhotoLoading,
                profileImageBitmap = profilePhotoImageBitmap,
                onMenuButtonClick = {}
            )
        }

    ) {
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                LocationMenu()
                Spacer(modifier = Modifier.width(35.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            SearchField(
                value = searchValue,
                onValueChange = onSearchValueChange,
                placeholder = "What are you looking for?",
                modifier = Modifier.padding(horizontal = if(isOrientationLandscape) 250.dp else 56.dp)
            )
            Spacer(Modifier.height(22.dp))
            CategoriesMenu(categoryList = CategoryList)
            Spacer(modifier = Modifier.height(29.dp))
            ProductsContainer(
                modifier = Modifier.fillMaxWidth(),
                title = "Latest",
                itemList = latestProductList
            )
            Spacer(modifier = Modifier.height(18.dp))
            ProductsContainer(
                modifier = Modifier.fillMaxWidth(),
                title = "Flash sale",
                itemList = flashSaleProductsList
            )
            Spacer(modifier = Modifier.height(18.dp))
            ProductsContainer(
                modifier = Modifier.fillMaxWidth(),
                title = "Brands",
                itemList = latestProductList
            )
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(it.calculateBottomPadding()))
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ProductsContainer(
    modifier: Modifier = Modifier,
    title: String = "test title",
    itemList: List<com.example.onlineshop.network.Product> = listOf()
) {
    Column(
        modifier,

        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 11.dp),
                fontSize = 15.sp,
                color = Color(0xff040402),
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins
            )
            Text(
                text = "View all",
                modifier = Modifier.padding(end = 11.dp),
                fontSize = 9.sp,
                color = Color(0xff808080),
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins
            )
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(itemList) { index: Int, item: com.example.onlineshop.network.Product ->
                when (item) {
                    is com.example.onlineshop.network.Latest -> {
                        LatestProductItem(
                            item = item,
                            modifier = Modifier
                                .height(150.dp)
                                .width(115.dp)
                                .padding(start = 12.dp)
                        )
                    }
                    is com.example.onlineshop.network.FlashSale -> {
                        FlashSaleProductItem(
                            item = item,
                            modifier = Modifier
                                .height(220.dp)
                                .width(175.dp)
                                .padding(start = 12.dp)
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}


@Composable
private fun LatestProductItem(
    modifier: Modifier = Modifier,
    item: com.example.onlineshop.network.Latest,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(9.dp)
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = item.image_url,
            contentDescription = null
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF252525)
                )
            } else {
                SubcomposeAsyncImageContent(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                ConstraintLayout {
                    val (category, name, price, button) = createRefs()
                    CategoryOfProductContainer(
                        modifier = Modifier
                            .constrainAs(category) {
                                start.linkTo(parent.start, 7.dp)
                                bottom.linkTo(parent.bottom, 46.dp)
                            }
                            .height(12.dp)
                            .widthIn(min = 35.dp),
                        text = item.category
                    )
                    Text(
                        modifier = Modifier
                            .widthIn(max = 75.dp)
                            .constrainAs(name) {
                                start.linkTo(parent.start, 7.dp)
                                top.linkTo(category.bottom, 4.dp)
                            },
                        maxLines = 2,
                        text = item.name,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.sp,
                        fontFamily = Poppins
                    )
                    Text(
                        modifier = Modifier
                            .widthIn(max = 75.dp)
                            .constrainAs(price) {
                                start.linkTo(parent.start, 7.dp)
                                bottom.linkTo(parent.bottom, 7.dp)
                            },
                        maxLines = 2,
                        text = "$ %,d".format(Locale.FRANCE, item.price),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 7.sp,
                        fontFamily = Poppins
                    )
                    androidx.compose.material.FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            hoveredElevation = 0.dp,
                            focusedElevation = 0.dp,
                        ),
                        onClick = { /*TODO*/ },
                        backgroundColor = Color(0xffe5e9ef).copy(alpha = 0.85f),
                        modifier = Modifier
                            .constrainAs(button) {
                                end.linkTo(parent.end, 5.dp)
                                bottom.linkTo(parent.bottom, 5.dp)
                            }
                            .size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color(0xff545589),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

            }
        }
    }

}

@Composable
private fun FlashSaleProductItem(
    modifier: Modifier = Modifier,
    item: com.example.onlineshop.network.FlashSale,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(9.dp)
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = item.image_url,
            contentDescription = null
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF252525)
                )
            } else {
                SubcomposeAsyncImageContent(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                ConstraintLayout {
                    val (category, name, price, addButton, likeButton, sale, imageIcon) = createRefs()
                    CategoryOfProductContainer(
                        modifier = Modifier
                            .constrainAs(category) {
                                start.linkTo(parent.start, 10.dp)
                                bottom.linkTo(parent.bottom, 83.dp)
                            }
                            .height(17.dp)
                            .widthIn(min = 50.dp),
                        fontSize = 9.sp,
                        text = item.category
                    )
                    Text(
                        modifier = Modifier
                            .widthIn(max = 90.dp)
                            .constrainAs(name) {
                                start.linkTo(parent.start, 10.dp)
                                top.linkTo(category.bottom, 6.dp)
                            },
                        maxLines = 2,
                        text = item.name,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        fontFamily = Poppins
                    )
                    Text(
                        modifier = Modifier
                            .widthIn(max = 90.dp)
                            .constrainAs(price) {
                                start.linkTo(parent.start, 10.dp)
                                bottom.linkTo(parent.bottom, 17.dp)
                            },
                        maxLines = 2,
                        text = "$ %,.2f".format(Locale.FRENCH, item.price),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        fontFamily = Poppins
                    )
                    androidx.compose.material.FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            hoveredElevation = 0.dp,
                            focusedElevation = 0.dp,
                        ),
                        onClick = { /*TODO*/ },
                        backgroundColor = Color(0xffe5e9ef).copy(alpha = 0.85f),
                        modifier = Modifier
                            .constrainAs(addButton) {
                                end.linkTo(parent.end, 4.dp)
                                bottom.linkTo(parent.bottom, 7.dp)
                            }
                            .size(35.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = Color(0xff545589),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    androidx.compose.material.FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            hoveredElevation = 0.dp,
                            focusedElevation = 0.dp,
                        ),
                        onClick = { /*TODO*/ },
                        backgroundColor = Color(0xffe5e9ef).copy(alpha = 0.85f),
                        modifier = Modifier
                            .constrainAs(likeButton) {
                                end.linkTo(addButton.start, 5.dp)
                                bottom.linkTo(addButton.bottom)
                                top.linkTo(addButton.top)
                            }
                            .size(28.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.heart),
                            contentDescription = null,
                            tint = Color(0xff545589),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    SaleContainer(
                        sale = item.discount,
                        modifier = Modifier
                            .constrainAs(sale) {
                                top.linkTo(parent.top, 7.dp)
                                end.linkTo(parent.end, 8.dp)
                            }
                            .height(18.dp)
                    )
                    ProfilePhotoContainer(
                        imageBitmap = ImageBitmap.imageResource(id = R.drawable.example_flash_sale_logo),
                        modifier = Modifier
                            .size(25.dp)
                            .constrainAs(imageIcon) {
                                top.linkTo(parent.top, 6.dp)
                                start.linkTo(parent.start, 10.dp)
                            },
                    )
                }

            }
        }
    }

}

@Composable
private fun CategoryOfProductContainer(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 6.sp,
    text: String = ""
) {
    Surface(
        modifier = modifier,
        color = Color(0xffc4c4c4).copy(alpha = 0.85f),
        shape = RoundedCornerShape(40)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.padding(horizontal = 6.dp),
                text = text,
                fontSize = fontSize,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xff070604)
            )
        }

    }
}

@Composable
private fun SaleContainer(
    modifier: Modifier = Modifier,
    sale: Int = 0,
) {
    Surface(
        modifier = modifier,
        color = Color(0xffF93A3A),
        shape = RoundedCornerShape(5.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.padding(horizontal = 6.dp),
                text = "$sale% off",
                fontSize = 10.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xff070604)
            )
        }
    }
}


@Composable
private fun LocationMenu(

) {
    Text(
        text = "Location",
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = Poppins,
        color = Color(0xff5b5b5b)
    )
    androidx.compose.material.IconButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.size(13.dp)
    ) {
        Icon(
            modifier = Modifier.size(13.dp),
            imageVector = Icons.Filled.ExpandMore,
            contentDescription = "choose location button",
        )
    }
}

@Composable
private fun CategoriesMenu(
    categoryList: List<CategoryItem>,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        categoryList.forEach { item ->
            CategoryItem(
                modifier = Modifier.weight(1f),
                imageVector = ImageVector.vectorResource(id = item.IconId),
                title = item.title
            )
        }
    }
}

@Composable
private fun CategoryItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,

    title: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = imageVector,
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            fontSize = 8.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = Poppins,
            textAlign = TextAlign.Center,
            color = Color(0xffA6A7AB)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    OnlineShopTheme {
        HomeScreenUi()
    }
}