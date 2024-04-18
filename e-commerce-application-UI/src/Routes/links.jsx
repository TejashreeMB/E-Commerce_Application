import Register from "../Public/Register"
export const links=[
    {
        path:"/customer/register",
        element:<Register ROLE= {"CUSTOMER"}/>
    },
    {
        path:"/seller/register",
        element:<Register ROLE= {"SELLER"}/>
    }
]