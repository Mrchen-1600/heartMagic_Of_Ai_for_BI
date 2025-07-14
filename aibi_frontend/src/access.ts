/**
 * @see https://umijs.org/docs/max/access#access
 * */
export default function access(initialState: InitialState) {
  const canUser = !!initialState.loginUser;
  const canAdmin = initialState.loginUser && initialState.loginUser.userRole === 'admin';
  return {
    canUser,
    canAdmin,
  };
}
