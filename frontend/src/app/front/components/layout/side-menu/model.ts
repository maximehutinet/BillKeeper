export interface MenuItem {
  label: string;
  icon?: string;
  link?: string;
  badgeValue?: number;
  items?: MenuItem[];
}
